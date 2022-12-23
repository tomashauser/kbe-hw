def reverse_xor_shift_left(x, shift, bits=32):
    res = x
    for _ in range(bits):
        res = x ^ (res >> shift)
    return res


def reverse_xor_shift_right_with_and_mask(x, shift, mask, bits=32):
    res = x
    for _ in range(bits):
        res = x ^ ((res << shift) & mask)
    return res

def num_to_hex(num):
    return str(hex(num)[2:]).zfill(8)
