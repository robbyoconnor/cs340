from collections import deque


class ReadyQueue(object):

    def __init__(self):
        self.queue = deque([])

    def add_pcb_to_readyqueue(self, pcb):
        self.queue.append(pcb)
        return pcb

    def is_cpu_busy(self):
        if len(self.queue) >= 1:
            return True
        return False

    def queue_len(self):
        return len(self.queue)

    def remove_and_return_pcb(self):
        pcb = self.queue.pop()
        return pcb

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
