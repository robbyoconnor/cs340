from collections import deque


class ReadyQueue(object):

    def __init__(self):
        self.queue = deque([])

    def add_pcb_to_readyqueue(self, pcb):
        self.queue.append(pcb)
        return pcb

    def queue_len(self):
        return len(self.queue)

    def pop(self):
        if self.queue_len() == 0:
            print("The CPU is currently empty -- cannot proceed")
        else:
            return self.queue.popleft()

    def print_readyqueue(self):
        print("Ready Queue\n")
        print("----- -----\n")
        for pcb in self.queue:
            print("%s\t%s\t%s\t%s\t%s"
                  % (pcb.pid,
                     pcb.file_name,
                     pcb.memory_start_region,
                     pcb.readwrite,
                     pcb.file_size))
