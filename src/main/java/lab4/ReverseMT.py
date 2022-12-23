import PwResetTokenGen
from MT import m, n, a, l, t, c, s, b, u, SIZE, MT
from utils import reverse_xor_shift_left, reverse_xor_shift_right_with_and_mask


class ReverseMT:
    def __init__(self, ptg: PwResetTokenGen):
        v = []
        self.index = (ptg.timesCalled * 15) % SIZE
        for i in range (0, 39):
            token = ptg.gen_token()
            intsArr = ReverseMT.token_to_int_arr(token)
            v.extend(intsArr)
        v = list(map(lambda x: ReverseMT.untemper(x), v))
        self.v = v

    @staticmethod
    def token_to_int_arr(token):
        nums = []
        for i in range(0, len(token), 8):
            nums.append(int(token[i:i + 8], 16))
        return nums

    def prev(self):
        if self.index == 0:
            self.v = ReverseMT.untwist(self.v)
            self.index = SIZE - 1
        x = self.v[self.index]
        self.index = self.index - 1
        return MT.temper(x)

    @staticmethod
    def untemper(v: int):
        v = reverse_xor_shift_left(v, l)
        v = reverse_xor_shift_right_with_and_mask(v, t, c)
        v = reverse_xor_shift_right_with_and_mask(v, s, b)
        v = reverse_xor_shift_left(v, u)
        return v

    @staticmethod
    def untwist(v_prev: [int]):
        v = v_prev.copy()
        for i in range(623, -1, -1):
            tmp = v[i] ^ v[(i + m) % n]
            if tmp & 0x80000000 == 0x80000000:
                tmp ^= a
            res = (tmp << 1) & 0x80000000
            firstIndex = (i + n - 1) % n
            secondIndex = (i + m - 1) % n
            tmp = v[firstIndex] ^ v[secondIndex]
            if tmp & 0x80000000 == 0x80000000:
                res = res | 1
                tmp ^= a
            res = res | ((tmp << 1) & 0x7FFFFFFF)
            v[i] = res
        return v


