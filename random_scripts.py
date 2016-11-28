import csv, pdb, copy
from random import randrange, randint
import time
import random
from datetime import timedelta, datetime
from dateutil.relativedelta import relativedelta

def random_date(start, end):
	delta = end - start
	int_delta = (delta.days * 24 * 60 * 60) + delta.seconds
	random_second = randrange(int_delta)
	return start + timedelta(seconds=random_second)

with open("data/routes.csv", "r") as in_file:
	with open("new_routes.csv", "w") as out_file:
		writer = csv.writer(out_file, lineterminator="\n")
		reader = csv.reader(in_file)
		all = []
		headers = next(reader)
		price_index = headers.index("Date")
		all.append(headers)
		for row in reader:
			row[price_index] = random_date(datetime.now(), datetime.now()+relativedelta(years=1)).strftime("%Y-%m-%d")
			all.append(row)
		writer.writerows(all)
