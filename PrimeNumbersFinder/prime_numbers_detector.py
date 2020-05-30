def is_prime_number(random_number, start, end):
    if random_number <= 1:
        return f'{random_number} number'
    else:
        for number in range(start, end):
            if random_number % number == 0 and random_number != number:
                print(f"{random_number} is divisible by {number}. {random_number} is not a prime number")
                return f"{random_number} is divisible by {number}. {random_number} is not a prime number"
        print(f"{random_number} is a prime number")
        return f"{random_number} is a prime number"

