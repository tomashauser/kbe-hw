w, n, m, r = 32, 624, 397, 31
a = 0x9908B0DF
u, d = 11, 0xFFFFFFFF
s, b = 7, 0x9D2C5680
t, c = 15, 0xEFC60000
l = 18
f = 1812433253
SIZE = 624

class MT:
    def __init__(self, seed):
        self.index = 0
        self.v = [0] * SIZE
        self.seed(seed)

    def seed(self, seed):
        self.v[0] = seed
        for i in range(1, SIZE):
            self.v[i] = (f * (self.v[i - 1] ^ (self.v[i - 1] >> 30)) + i) & 0xffffffff
        self.index = n

    def next(self) -> int:
        if self.index == n:
            self.v = self.twist(self.v)
            self.index = 0
        x = self.v[self.index]
        self.index = self.index + 1
        return self.temper(x)

    @staticmethod
    def twist(v_prev: [int]) -> [int]:
        v = v_prev.copy()
        for i in range(0, SIZE):
            x = (v[i] & 0x80000000) ^ (v[(i + 1) % n] & 0x7fffffff)
            xA = x >> 1
            if x & 0x00000001:
                xA = xA ^ a
            newVal = v[(i + m) % n] ^ xA
            v[i] = newVal
        return v

    @staticmethod
    def temper(x: int) -> int:
        y1 = x ^ ((x >> u) & d)
        y2 = y1 ^ ((y1 << s) & b)
        y3 = y2 ^ ((y2 << t) & c)
        z = y3 ^ (y3 >> l)
        return z