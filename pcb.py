# Represents a Process Control Block
current_pid = 0


class PCB(object):

    def __init__(self, memory_start_region="", readwrite="",
                 file_name="", file_size=""):
        self.pid = ++current_pid
        self.memory_start_region = memory_start_region
        self.readwrite = readwrite
        self.file_name = file_name
        self.file_size = file_size

    @classmethod
    def getCurrentPid(self):
        return self.pid
