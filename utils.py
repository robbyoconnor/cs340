"""utils - various functions that help with input validation and error trapping
"""


def does_not_contain_spaces(user_input):
    # check for spaces...
    if(' ' in user_input):
        return False
    else:
        return True


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
    matcher = re.compile(r"^[AtS]{1}$|^[pPdDcC]\d+$")  # validate input
    return matcher.match(user_input) is not None
