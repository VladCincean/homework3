int main()
{
	typedef int userint;

	userint n;
	userint sum;
	userint x;

	cout << "n = ";
	cin >> n;

	if (n < 1)
	{
		cout << "Sorry, you gave a non-positive n" << endl;
		return 0;
	}

	cout << "Please, input " << n << " numbers to add together." << endl;

	sum = 0;

	while (n > 0)
	{
		cin >> x;
		sum = sum + x;
		n = n - 1;
	}

	cout << "sum = " << sum << endl;

	return 0;
}
