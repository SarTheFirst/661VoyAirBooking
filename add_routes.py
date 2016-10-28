import csv, pdb, copy
from random import randrange, randint
import time
from dateutil.relativedelta import relativedelta
from datetime import timedelta, datetime
def random_date(start, end):
	delta = end - start
	int_delta = (delta.days * 24 * 60 * 60) + delta.seconds
	random_second = randrange(int_delta)
	return start + timedelta(seconds=random_second)

with open("data/routes.dat", "r") as in_file:
	with open("new_routes.csv", "w") as out_file:
		writer = csv.writer(out_file, lineterminator="\n")
		reader = csv.reader(in_file)
		all = []
		headers = next(reader)
		headers += ["time", "date"]
		all.append(headers)
		datecounter = 0

		for row in reader:
			for i in range(5):
				org_row = copy.deepcopy(row)
				randomdate = random_date(datetime.now(), datetime.now()+ relativedelta(years=1, seconds=randint(10,40)))
				org_row += [randomdate.time().strftime("%H:%M"), randomdate.date()]
				all.append(org_row)
		writer.writerows(all)
