int main()
{
	double radius;
	double area;
	double perimeter;

	cout << "radius: ";
	cin >> radius;

	if (radius <= 0)
	{
		cout << "The radius cannot be negative." << endl;
	}

	perimeter = 2 * 3.14 * radius;
	area = 3.14  * radius * radius;

	cout << "perimeter: " << perimeter << endl;
	cout << "area: " << area << endl;

	return 0;
}
