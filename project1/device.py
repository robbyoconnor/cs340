
from collections import deque


class Device(object):

    def __init__(self, number):
        self.queue = deque([])
        self.number = number

    def append(self, pcb):
        self.queue.append(pcb)
        return pcb

    def size(self):
        return len(self.queue)

    def pop(self):
        return self.queue.pop()

    def print_queue(self):
        print("%s Queue" % self.name)
        for pcb in self.queue:
            print("%s\t%s\t%s\t%s\t\t%s"
                  % (pcb.pid,
                     pcb.file_name,
                     pcb.memory_start_region,
                     pcb.readwrite,
                     pcb.file_size))


class Printer(Device):

    def __init__(self, number):
        Device.__init__(self, number)
        self.name = "Printer %d" % int(number)


class CDRW(Device):

    def __init__(self, number):
        Device.__init__(self, number)
        self.name = "CDRW %d" % int(number)


class Disk(Device):

    def __init__(self, number):
        Device.__init__(self, number)
        self.name = "Disk %d" % int(number)
