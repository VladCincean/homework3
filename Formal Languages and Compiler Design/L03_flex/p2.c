int main()
{
	int a;
	int b;
	int c;

	cout << "first number: ";
	cin >> a;

	cout << "second number: ";
	cin >> b;

	while (b != 0)
	{
		c = b;
		b = a % b;
		a = c;
	}

	cout << "gcd = " << a << endl;

	return 0;
}
