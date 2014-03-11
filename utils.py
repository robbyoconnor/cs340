"""utils - various functions that help with input validation and error trapping
"""


def check_integer(user_input):
    n = 0
    try:
        n = int(user_input)
    except ValueError:
        return n
    return n


def check_case(user_input, upper=False):
    match_string = r""
    if upper:
        match_string = r"[A-Z]"
    else:
        match_string = r"[a-z]"
    import re
    match = re.match(match_string, user_input)
    if match:
        return True
    return False


def validate_input(user_input):
    import re
    # validate input
    matcher = re.compile(r"^[AtSQq]{1}$|^[pPdDcC]\d+$|^[rdpc]{1}$")
    return matcher.match(user_input) is not None


def parse_device_information(user_input):
    import re
    regex = re.compile(r"^([pPdDcC])(\d+)$")
    matcher = regex.match(input)
    return (matcher.group(1), matcher.group(1))


def parse_if_terminate_syscall(user_input):
    import re
    return re.compile("^t{1}$").match(user_input) is not None


def do_we_quit(user_input):
    import re
    matcher = re.compile(r"^[Qq]{1}")  # validate input
    if matcher.match(user_input) is not None:
        return True
    return False
