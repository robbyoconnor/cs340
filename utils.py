"""utils - various functions that help with input validation and error trapping
"""


def check_integer(user_input):
    return match(r"^\d+$", user_input)


def validate_input(user_input):
    return match(r"^[AtSQq]{1}$|^[pPdDcC]\d+$", user_input)


def validate_input_snapshot(user_input):
    return match(r"^[rdpc]{1}$", user_input)


def parse_if_terminate_syscall(user_input):
    return match(r"^t{1}", user_input)


def do_we_quit(user_input):
    return match(r"^[Qq]{1}", user_input)


def match(pattern, matchStr):
    import re
    return re.compile(pattern).match(matchStr) is not None

