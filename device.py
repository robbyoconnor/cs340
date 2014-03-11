
from collections import deque


class Device(object):

    def __init__(self, number):
        self.queue = deque([])
        self.number = number


class Printer(Device):

    def __init__(self, number):
        Device.__init__(self, number)
        self.name = "Printer %d" % int(number)

    def print_cdrw_queue(self):
        if self.queue.len == 0:
            print("Nothing in the %s Queue!" % self.name)
        else:
            print("%s Queue\n" % self.name)
            print("---\t---------\t--------\t---\t-----------\n")
            for pcb in self.queue:
                print("%s\t%s\t%s\t%s\t%s"
                      % (pcb.pid,
                         pcb.file_name,
                         pcb.memory_start_region,
                         pcb.readwrite,
                         pcb.file_size))


class CDRW(Device):

    def __init__(self, number):
        Device.__init__(self, number)
        self.name = "CDRW %d" % int(number)

    def print_cdrw_queue(self):
        if self.queue.len == 0:
            print("Nothing in the %s Queue!" % self.name)
        else:
            print("%s Queue\n" % self.name)
            print("---\t---------\t--------\t---\t-----------\n")
            for pcb in self.queue:
                print("%s\t%s\t%s\t%s\t%s"
                      % (pcb.pid,
                         pcb.file_name,
                         pcb.memory_start_region,
                         pcb.readwrite,
                         pcb.file_size))


class Disk(Device):

    def __init__(self, number):
        Device.__init__(self, number)
        self.name = "Disk %d" % int(number)

    def print_cdrw_queue(self):
        if self.queue.len == 0:
            print("Nothing in the %s Queue!" % self.name)
        else:
            print("%s Queue\n" % self.name)
            print("---\t---------\t--------\t---\t-----------\n")
            for pcb in self.queue:
                print("%s\t%s\t%s\t%s\t%s"
                      % (pcb.pid,
                         pcb.file_name,
                         pcb.memory_start_region,
                         pcb.readwrite,
                         pcb.file_size))
