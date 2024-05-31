def mymax(iterable, key=lambda x: x):
    max_x = max_key = None

    for x in iterable:
        if max_x is None or key(x) > max_key:
            max_x = x
            max_key = key(x)

    return max_x

words = ["iva", "ella", "dominik"]
print("The longest word:", mymax(words, key=lambda x: len(x)))

print("MaxINT:", mymax([1, 3, 5, 7, 4, 6, 9, 2, 0]))
print("MaxCHAR:", mymax("Suncana strana ulice"))
print("MaxSTRING:", mymax([
  "Gle", "malu", "vocku", "poslije", "kise",
  "Puna", "je", "kapi", "pa", "ih", "njise"]))

D = {'burek': 8, 'buhtla': 5}
print("The most expensive product in the bakery:", mymax(D, key=lambda x: D.get(x)))

people = [('Zva', 'Goluzzza'), ('Iva', 'Goluza'), ('Ella', 'Prezime')]
last_person = mymax(people)
print("The last lexi person:", last_person)