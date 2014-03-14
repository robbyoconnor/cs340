from utils import (check_integer, validate_input, parse_if_terminate_syscall,
                   do_we_quit, validate_input_snapshot, match)

from device import Printer, CDRW, Disk
from myos import ReadyQueue
from pcb import PCB, populatePCB


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
        print(r"With the exception of 'A' and 't'")

    def sysgen(self):
        print("Welcome to sysgen!")

        printer_cnt = input("How many printers: ").strip()
        while not check_integer(printer_cnt):
            printer_cnt = input(
                "Not a positive integer, enter a positive integer value: ")

        disk_cnt = input("How many disks: ").strip()
        while not check_integer(disk_cnt):
            disk_cnt = input(
                "Not a positive integer, enter a positive integer value: ")

        cdrw_cnt = input("How many cdrw drives: ").strip()
        while not check_integer(cdrw_cnt):
            cdrw_cnt = input(
                "Not a positive integer, enter a positive integer value: ")

        if int(printer_cnt) > 0:
            self.printers = [Printer(n + 1)
                             for n in range(0, int(printer_cnt))]

        if int(disk_cnt) > 0:
            self.disks = [Disk(n + 1) for n in range(0, int(disk_cnt))]

        if int(cdrw_cnt) > 0:
            self.cdrws = [CDRW(n + 1) for n in range(0, int(cdrw_cnt))]

    def run(self):
        print(r"We are running...")
        os.help()
        keep_running = True
        while keep_running:

            user_input = input("[A,S,t,p#,d#,c#,P#,D#,C#]: ").strip()
            while not validate_input(user_input):
                user_input = input("Not valid! Try again: ")
            # We know this is valid now....
            if user_input == "A":
                pcb = PCB()
                self.readyQueue.add_pcb_to_readyqueue(pcb)
                print("Added process %d " % pcb.pid)
            elif user_input == "S":
                selection = input("[r,d,p,c]: ")
                while not validate_input_snapshot(selection):
                    selection = input("Invalid input! [r,d,p,c]: ")
                if selection == "r":
                    self.snapshot(readyQ=True)
                elif selection == "d":
                    self.snapshot(diskQ=True)
                elif selection == "p":
                    self.snapshot(printerQ=True)
                elif selection == "c":
                    self.snapshot(cdrwQ=True)
                else:
                    print("If we get here...fail me.")
            elif match(r"^[PDCpdc]{1}\d+$", user_input):
                if user_input[0].islower():
                    self.syscall(user_input)
                elif user_input[0].isupper():
                    self.interrupt(user_input)
                else:
                    print("If you see this message...dock points!")
            elif parse_if_terminate_syscall(user_input):

                if self.readyQueue.queue_len() > 0:
                    pcb = self.readyQueue.pop()
                    print("Terminated Process %d " % pcb.pid)
                else:
                    print("No process is currently in the CPU.")

            elif do_we_quit(user_input):
                print(
                    "This always happens...was it me??!! I love you anyways...")
                keep_running = False  # break out.

    def snapshot(self, diskQ=False, printerQ=False, cdrwQ=False, readyQ=False):

        def print_field_heading():
            print("PID\tFile name\tMemStart\tR/W\tFile Length\n")
            print("---\t---------\t---------\t---\t----------\n")

        if readyQ:
            if self.readyQueue.queue_len():
                print_field_heading()
                self.readyQueue.print_readyqueue()
            else:
                print("Nothing in the ready queue!")
        if diskQ:
            print_field_heading()
            for disk in self.disks:
                disk.print_queue()
        elif printerQ:
            print_field_heading()
            for printer in self.printers:
                printer.print_queue()
        elif cdrwQ:
            print_field_heading()
            for cdrw in self.cdrws:
                cdrw.print_queue()

    def syscall(self, user_input):
        if user_input[0] == "p":
            if int(user_input[1:]) > len(self.printers):
                print("There is no printer %s -- valid: 1-%d" %
                      (user_input[1:], len(self.printers)))
            elif self.readyQueue.queue_len() == 0:
                print("Nothing is in the CPU!")
            else:
                pcb = self.readyQueue.pop()
                populatePCB(pcb, True)
                self.printers[int(user_input[1:]) - 1].append(pcb)
                print("Process %s has been sent to printer %s" %
                      (pcb.pid, user_input[1:]))
        elif user_input[0] == "d":
            if int(user_input[1:]) > len(self.disks):
                print("There is no disk %s -- valid: 1-%d" %
                      (user_input[1:], len(self.disks)))
            elif self.readyQueue.queue_len() == 0:
                print("Nothing is in the CPU!")
            else:
                pcb = self.readyQueue.pop()
                populatePCB(pcb)
                self.disks[int(user_input[1:]) - 1].append(pcb)
                print("Process %s has been sent to disk %s" %
                      (pcb.pid, user_input[1:]))
        elif user_input[0] == "c":
            if int(user_input[1:]) > len(self.cdrws):
                print("There is no cdrw %s -- valid: 1-%d" %
                      (user_input[1:], len(self.cdrws)))
            elif self.readyQueue.queue_len() == 0:
                print("Nothing is in the CPU!")
            else:
                pcb = self.readyQueue.pop()
                populatePCB(pcb)
                self.cdrws[int(user_input[1:]) - 1].append(pcb)
                print("Process %s has been sent to cdrw %s" %
                      (pcb.pid, user_input[1:]))

    def interrupt(self, user_input):
        if user_input[0] == "P":
            if int(user_input[1:]) > len(self.printers):
                print("There is no printer %s -- valid: 1-%d" %
                      (user_input[1:], len(self.printers)))
                return
            elif len(self.printers[int(user_input[1:]) - 1].queue) == 0:
                print("There is nothing in printer %s!" % user_input[1:])
                return
            else:
                pcb = self.printers[int(user_input[1:]) - 1].pop()
                self.readyQueue.add_pcb_to_readyqueue(pcb)
                print(
                    "Process %s is finished in Printer %s and has been moved back to the ready queue." % (pcb.pid, user_input[1:]))
        elif user_input[0] == "D":
            if int(user_input[1:]) > len(self.printers):
                print("There is no disk %s -- valid: 1-%d" %
                      (user_input[1:], len(self.printers)))
                return
            elif len(self.disks[int(user_input[1:]) - 1].queue) == 0:
                print("There is nothing in disk %s!" % user_input[1:])
                return
            else:
                pcb = self.disks[int(user_input[1:]) - 1].pop()
                self.readyQueue.add_pcb_to_readyqueue(pcb)
                print(
                    "Process %s is finished in disk %s and has been moved back to the ready queue." % (pcb.pid, user_input[1:]))
        elif user_input[0] == "C":
            if int(user_input[1:]) > len(self.cdrws):
                print("There is no cdrw %s -- valid: 1-%d" %
                      (user_input[1:], len(self.printers)))
                return
            elif len(self.cdrws[int(user_input[1:]) - 1].queue) == 0:
                print("There is nothing in cdrw %s!" % user_input[1:])
                return
            else:
                pcb = self.cdrws[int(user_input[1:]) - 1].pop()
                self.readyQueue.add_pcb_to_readyqueue(pcb)
                print(
                    "Process %s is finished in CDRW %s and has been moved back to the ready queue." % (pcb.pid, user_input[1:]))


if __name__ == '__main__':
    os = runit()
    os.sysgen()
    os.run()
