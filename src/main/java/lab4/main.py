from MT import MT
from PwResetTokenGen import PwResetTokenGen
from ReverseMT import ReverseMT
from utils import num_to_hex


def recover_token():
    ptg = PwResetTokenGen(123456789)
    timesCalled = 20
    for i in range(0, timesCalled - 1):
        ptg.gen_token()
    secretToken = ptg.gen_token()
    rmt = ReverseMT(ptg)
    prevs = []
    for i in range(0, 31 * timesCalled):
        prevs.append(rmt.prev())
    prevs.reverse()
    start = 16 * (timesCalled - 1)
    prevs = prevs[start: start + 16]
    recoveredToken = ''
    for i in range(0, len(prevs)):
        recoveredToken += num_to_hex(prevs[i])
    assert secretToken == recoveredToken


def test_untemper(x):
    tempered = MT.temper(x)
    untempered = ReverseMT.untemper(tempered)
    assert untempered == x

def test_untwist():
    arr = []
    for i in range(1, 625):
        arr.append(i)
    v = MT.twist(arr)
    v_prev = ReverseMT.untwist(v)
    for i in range(1, len(arr)):
        assert v_prev[i] == arr[i]

# 1. d) 0xffffffff is in binary: 0111 1111 1111 1111 1111 1111 1111 1111
#       Doing X & 0xffffffff leaves the number as is, but ensures that the first bit (the sign bit) will be 0 in the result,
#       thus ensuring that the result is always positive.
#       0x80000000 on the other hand is in binary: 1000 0000 0000 0000 0000 0000 0000 0000
#       Doing X & 0x80000000 turns the result into 0 if X is positive and into 0x80000000 if it is negative.
#
#       If we multiply the vector x with the matrix A, we obtain:
#       a * x_0 + [0, x_31, x_30, ..., x_1]
#       When we first do xA = xA = x >> 1, we obtain the second vector [0, x_31, x_30, ..., x_1].
#       The condition if (x & 0x00000001) just checks whether x_0 = 1. If it's 0, we don't need to do anything
#       as a * x_0 would evaluate to 0. If, on other hand, x ends with 1, meaning that x_0 = 1, we compute the addition.
#
#       Determinant of the given matrix is equal to -a_31; therefore the 31st bit of the constant "a" must not be equal
#       to 0 in order for the matrix to be regular. The constant 0x9908B0DF satisfies this condition.
#
# 2  b) We can multiply the two matrices to see if we get the identity matrix I as a result.
#       Apart from the last row, we just multiply two identity matrices, but in the last row, we get  2*a_30...2*a_0, 1.
#       Since we're in Z_2, it holds that 2x ≡ 0 (mod 2); therefore 2 * a_i = 0 and hence the last row is 0 0 ... 0 1
#       and the result is, in fact, an identity matrix.

if __name__ == '__main__':
    mt = MT(123456789)
    test_untwist()
    test_untemper(123456789)
    recover_token()


