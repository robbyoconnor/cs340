from utils import (check_integer, validate_input, parse_if_terminate_syscall,
                   do_we_quit, remove_and_return_pcb)

from device import Printer, CDRW, Disk
from myos import ReadyQueue
import PCB


class runit(object):

    def __init__(self):
        self.printers = []
        self.disks = []
        self.cdrws = []
        self.readyQueue = ReadyQueue()

    def help(self):
        print(r"Welcome to the OS...Here's how it works: ")
        print(r"'A' will create a new process (exclude quotes).")
        print(r"Lower case letters are system calls. e.g., p1")
        print(r"Upper case letters are interrupts.e.g., 'P1' for printer 1.")
        print(r"System calls can be made to access the following devices:")
        print(r"Printers, CDRW drives, and Disk drives")
        print(r"To kill a process, type 't'")
        print(r"To quit me, type 'q' or 'Q'")
        print(r"With the exception of 'A' and 't' -- \
            it expected that inputs contain two chracters with no spaces!")

    def sysgen(self):
        print("Welcome to sysgen! Let's set this shit up...")

        printer_cnt = raw_input("How many printers: ")
        while not check_integer(printer_cnt):
            printer_cnt = raw_input("Not an integer, enter an integer value: ")

        disk_cnt = raw_input("How many disks: ")
        while not check_integer(disk_cnt):
            disk_cnt = raw_input("Not an integer, enter an integer value: ")

        cdrw_cnt = raw_input("How many cdrw drives: ")
        while not check_integer(cdrw_cnt):
            cdrw_cnt = raw_input("Not an integer, enter an integer value: ")

        self.printers = [Printer(n + 1) for n in range(0, int(printer_cnt))]
        self.disks = [Disk(n + 1) for n in range(0, int(disk_cnt))]
        self.cdrws = [CDRW(n + 1) for n in range(0, int(cdrw_cnt))]

    def run(self):
        print(r"The fucking os is now running.")
        print(r"To quit this shit, type 'q' or 'Q' or Ctrl+C -- I don't care.")
        os.help()
        keep_running = True
        while keep_running:

            input = raw_input("[A,t,p#,d#,c#,P#,D#,C#]: ")
            while not validate_input(input):
                input = raw_input("Not valid! Try again: ")
            # We know this is valid now....
            # check exist condition...
            if do_we_quit(input):
                print("This always happens...was it me??!!")
                keep_running = False
            if parse_if_terminate_syscall(input):
                if readyQueue.queue_len():
                    pcb = remove_and_return_pcb()
                    print("Terminated process id %d".pcb.pid)

        def snapshot(self, diskQ=False, printerQ=False, cdrwQ=False,
                     readyQ=False):

            print("PID\tFile name\tMemStart\tR/W\tFile Length\n")
            if readyQ:
                self.readyQueue.print_readyqueue()
            if diskQ:
                for disk in self.disks:
                    disk.print_disk_queue()
            elif printerQ:
                for printer in self.printers:
                    printer.print_printer_queue()
            elif cdrwQ:
                for cdrw in self.cdrws:
                    printer.print_printer_queue()


if __name__ == '__main__':
    # while True:
    os = runit()
    os.help()
    os.sysgen()
    os.run()
