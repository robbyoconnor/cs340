from utils import check_integer, match
current_pid = 0



class PCB(object):

    def __init__(self, memory_start_region="", readwrite="",

                 file_name="", file_size=""):
        global current_pid
        self.pid = current_pid + 1
        current_pid += 1
        self.memory_start_region = memory_start_region
        self.readwrite = readwrite
        self.file_name = file_name
        self.file_size = file_size

    @classmethod
    def getCurrentPid(self):
        return current_pid


def populatePCB(pcb, printer=False):
    file_name = input("Give me a file name: ")
    file_size = input("How big is the file: ")
    while not check_integer(file_size):
        file_size = input("Only enter integers for file size try again: ")
    memory_start_region = input("Enter a memory start region: ")
    while not check_integer(memory_start_region):
        memory_start_region = input(
            "Must be an integer. Enter a memory start region: ")
    if printer:
        pcb.readwrite = "w"
    else:
        pcb.readwrite = input("Is this a read or write (r/w): ")
    while match(r"^[rw]{1}$", pcb.readwrite) is False:
        pcb.readwrite = input(
            "Invalid input for read/write! You must only enter r or w! Try again: ")

    pcb.file_size = file_size
    pcb.file_name = file_name
    pcb.memory_start_region = memory_start_region
    return pcb
