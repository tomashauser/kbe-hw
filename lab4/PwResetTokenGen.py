from MT import MT
from utils import num_to_hex


class PwResetTokenGen:
    def __init__(self, seed):
        self.mt = MT(seed)
        self.timesCalled = 0
        pass

    def gen_token(self):
        token = ""
        for i in range(0, 16):
            token += num_to_hex(self.mt.next())
        self.timesCalled = self.timesCalled + 1
        return token
