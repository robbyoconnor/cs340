from utils import check_integer, validate_input, check_case
from device import Printer, CDRW, Disk
import os


class runit(object):

    def __init__(self):
        self.printers = []
        self.disks = []
        self.cdrws = []

    def help(self):
        print("Welcome to the fucking OS...Here's how it works: \n")
        print("\'A\' will create a new process (exclude quotes)\n")
        print("Lower case letters are system calls.\n")
        print("Upper case letters are interrupts.\n")
        print("System calls can be made to access the following devices:\n")
        print("Printers, CDRW drives, and Disk drives\n")

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

if __name__ == '__main__':
    os = runit()
    os.help()
    os.sysgen()