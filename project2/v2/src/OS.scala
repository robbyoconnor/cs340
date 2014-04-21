class OS {

  import scala.collection.mutable.ArrayBuffer

  var printers = new ArrayBuffer[Printer]
  var cdrws = new ArrayBuffer[CDRW]
  var disks = new ArrayBuffer[Disk]
  var readyqueue = new ReadyQueue

  var alpha: Float = 0.0f

  var initialTau: Int = 0


  def help() = {
    println(s"Welcome to the OS...Here's how it works: ")
    println(s"'A' will create a new process (exclude quotes).")
    println(s"Lower case letters are system calls. e.g., p1")
    println(s"Upper case letters are interrupts.e.g., 'P1' for printer 1.")
    println(s"System calls can be made to access the following devices:")
    println(s"Printers, CDRW drives, and Disk drives")
    println(s"To kill a process, type 't'")
    println(s"To quit me, type 'q' or 'Q'")
    println(s"With the exception of 'A' and 't'")
  }


  def sysgen() = {
    println("Welcome to sysgen! I'll be your guide...let's set up the system!")
    print("How many printers: ")
    var printerCnt = Utils.promptForInt()

    for (i <- 0 to printerCnt-1) printers += new Printer(i + 1)

    print("How many disks: ")
    var disk_cnt = Utils.promptForInt()

    disks = Utils.generateDisks(disk_cnt)

    print("How many CDRWs: ")
    var cdrw_cnt = Utils.promptForInt()

    for (i <- 0 to cdrw_cnt-1) cdrws += new CDRW(i + 1)

    print("Enter initial burst estimate (tau) (integer greater than 0): ")
    initialTau = Utils.promptForInt()

    print("Enter history parameter (alpha) (float between 0 and 1): ")
    alpha = Utils.promptForFloat(0.0f,1.0f)

  }

  /**
   *
   * def run(self):
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

   */
  def run() = {
    help()
    var done = false
    do {
      var userInput = readLine("[A,S,t,p#,d#,c#,P#,D#,C#]:").replaceAllLiterally( """\s+""", "")
      while(!Utils.validateInput(userInput)) {
        println("Invalid input")
        userInput = readLine("[A,S,t,p#,d#,c#,P#,D#,C#]:").replaceAllLiterally( """\s+""", "")
      }

      if(userInput == "A") {
        var pcb = new PCB
        readyqueue.enqueue(pcb)
        println(s"Added process ${pcb.pid}")

      } else if(userInput == "S") {

      } else if(userInput == "t") {
      } else if(userInput == "Q" || userInput == "q") {
        done = true
      }
      var tuple: (String, Int) = Utils.extractDeviceInfo(userInput)
      if(Utils.isInterrupt(userInput)) {
        if(tuple._1 == "C") {

        } else if(tuple._1 == "D") {

        } else if(tuple._1 == "P") {

        }
      } else {
        if (tuple._1 == "c") {

        } else if (tuple._1 == "d") {

        } else if (tuple._1 == "p") {

        }
      }
    }while(!done)

  }

}

object RunThis extends App {
  var os = new OS
  os.sysgen()
  os.run()
}