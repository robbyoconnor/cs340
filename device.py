
from collections import deque


class Device(object):

    def __init__(self, number):
        self.queue = deque([])
        self.number = number


class Printer(Device):

    def __init__(self, number):
        Device.__init__(self,number)
        self.type = "Printer %d" % int(number)


class CDRW(Device):

    def __init__(self, number):
        Device.__init__(self,number)
        self.type = "CDRW %d" % int(number)


class Disk(Device):

    def __init__(self, number):
        Device.__init__(self,number)
        self.type = "Disk %d" % int(number)

# test
printer = Printer(3)
