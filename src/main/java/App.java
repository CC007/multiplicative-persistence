import com.google.common.primitives.Chars;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javafx.util.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class App
{
	public static final int STEPS_REQUIRED = 8;
	public static final int MAX_PERMUTATION_COUNT = 20;

	public static void main(String[] args)
	{
		long start = System.currentTimeMillis();
		Pair<BigInteger, Integer> number = findNumber(STEPS_REQUIRED);
		if (checkNumber(number.getKey(), STEPS_REQUIRED))
		{
			System.out.println("Found number: " + number.getKey() + " (in " + number.getValue() + " steps)");
		}
		else
		{
			System.out.println("Wrong number found! Number: " + number.getKey() + " (in " + number.getValue() + " steps)");
		}
		System.out.println("Total time: " + (System.currentTimeMillis() - start) / 1000.0);
	}

	@NotNull
	public static Pair<BigInteger, Integer> findNumber(int stepsRequired)
	{
		Pair<BigInteger, Integer> number = null;
		int startingNumber = 4; // first non-prime > 1
		while (number == null)
		{
			System.out.println("Trying starting number: " + startingNumber);
			number = getNumber(BigInteger.valueOf(startingNumber), stepsRequired);
			startingNumber++;
		}
		return number;
	}

	@Nullable
	public static Pair<BigInteger, Integer> getNumber(BigInteger number, int stepsRequired)
	{
		return getNumber(number, stepsRequired, getStepsForNumber(number));
	}

	@Nullable
	public static Pair<BigInteger, Integer> getNumber(BigInteger number, int stepsRequired, int currentSteps)
	{
		//System.out.println("get new number with number: " + number + " (currently in " + currentSteps + " steps)");
		if (stepsRequired == currentSteps)
		{
			//System.out.println("Found!");
			return new Pair<>(number, currentSteps);
		}
		List<BigInteger> factors = primeBigFactorize(number);
		if (factors.size() < 1)
		{
			//System.out.println("Not found! Factors higher than 9 found.");
			return null;
		}
		if (factors.size() < 2)
		{
			//System.out.println("Not found! Not enough factors found.");
			return null;
		}
		if (factors.stream().anyMatch(f -> f.compareTo(BigInteger.valueOf(10)) >= 0))
		{
			//System.out.println("Not found! Factors higher than 9 found: " + factors.toString());
			return null;
		}
		factors.add(BigInteger.valueOf(1));
		factors.add(BigInteger.valueOf(1));
		Pair<BigInteger, Integer> newNumber;
		for (BigInteger bigInteger : getValidNumberListFromDigits(factors))
		{
			newNumber = getNumber(bigInteger, stepsRequired, currentSteps + 1);
			if(newNumber != null){
				return newNumber;
			}
		}
		return null;
	}

	public static List<BigInteger> getValidNumberListFromDigits(List<BigInteger> digits){
		List<BigInteger> result = getValidNumbersFromDigits(digits).stream().sorted().collect(Collectors.toList());
		//System.out.println(result);
		return result;
	}

	public static Set<BigInteger> getValidNumbersFromDigits(List<BigInteger> digits)
	{
		List<BigInteger> newDigits;

		// add number with current digits to result
		//Set<List<BigInteger>> digitPermutations = permutations(digits.stream().sorted().collect(Collectors.toList()), MAX_PERMUTATION_COUNT);
		Set<List<BigInteger>> digitPermutations = Stream.of(digits).collect(Collectors.toSet());
		Set<BigInteger> numbers = digitPermutations.stream().map(App::bigIntFromDigits).collect(Collectors.toSet());

		if(digits.size() > 2)
		{
			//case where the factors contain at least 2 2s
			if (digits.stream().filter(f -> f.compareTo(BigInteger.valueOf(2)) == 0).count() >= 2)
			{
				newDigits = new ArrayList<>(digits);
				newDigits.remove(BigInteger.valueOf(2));
				newDigits.remove(BigInteger.valueOf(2));
				newDigits.add(BigInteger.valueOf(4));
				numbers.addAll(getValidNumbersFromDigits(newDigits));
			}
			//case where the factors contain at least 2 3s
			if (digits.stream().filter(f -> f.compareTo(BigInteger.valueOf(3)) == 0).count() >= 2)
			{
				newDigits = new ArrayList<>(digits);
				newDigits.remove(BigInteger.valueOf(3));
				newDigits.remove(BigInteger.valueOf(3));
				newDigits.add(BigInteger.valueOf(9));
				numbers.addAll(getValidNumbersFromDigits(newDigits));
			}
			//case where the factors contain at least a 2 and a 3
			if (digits.contains(BigInteger.valueOf(2)) && digits.contains(BigInteger.valueOf(3)))
			{
				newDigits = new ArrayList<>(digits);
				newDigits.remove(BigInteger.valueOf(2));
				newDigits.remove(BigInteger.valueOf(3));
				newDigits.add(BigInteger.valueOf(6));
				numbers.addAll(getValidNumbersFromDigits(newDigits));
			}
			//case where the factors contain at least a 2 and a 4
			if (digits.contains(BigInteger.valueOf(2)) && digits.contains(BigInteger.valueOf(4)))
			{
				newDigits = new ArrayList<>(digits);
				newDigits.remove(BigInteger.valueOf(2));
				newDigits.remove(BigInteger.valueOf(4));
				newDigits.add(BigInteger.valueOf(8));
				numbers.addAll(getValidNumbersFromDigits(newDigits));
			}
		}
		return numbers;
	}

	public static BigInteger bigIntFromDigits(List<BigInteger> digits)
	{
		return new BigInteger(digits.stream().map(String::valueOf).collect(Collectors.joining("")));
	}

	public static boolean checkNumber(BigInteger number, int expectedSteps)
	{
		return expectedSteps == getStepsForNumber(number, true);
	}

	public static int getStepsForNumber(BigInteger number, boolean log)
	{
		BigInteger newNumber = number;
		System.out.println(newNumber);
		int actualSteps = 0;
		while (newNumber.toString().length() > 1)
		{
			newNumber = step(newNumber);
			System.out.println(newNumber);
			actualSteps++;
		}
		System.out.println("Steps: " + actualSteps);
		return actualSteps;
	}

	public static int getStepsForNumber(BigInteger number)
	{
		BigInteger newNumber = number;
		int actualSteps = 0;
		while (newNumber.toString().length() > 1)
		{
			newNumber = step(newNumber);
			actualSteps++;
		}
		return actualSteps;
	}


	public static BigInteger step(BigInteger number)
	{
		return Chars.asList(number.toString().toCharArray())
			.stream().map(c -> BigInteger.valueOf(Integer.parseInt(String.valueOf(c))))
			.reduce(BigInteger.valueOf(1), BigInteger::multiply);
	}

	public static ArrayList<BigInteger> primeBigFactorize(BigInteger n)
	{
		ArrayList<BigInteger> primeFactors = new ArrayList<>();
		BigInteger primeFactor = BigInteger.ZERO;

		for (BigInteger i = new BigInteger("2"); i.compareTo(n.divide(i)) <= 0; )
		{
			if (n.mod(i).longValue() == 0)
			{
				primeFactor = i;
				if(primeFactor.compareTo(BigInteger.valueOf(10)) >= 0){
					return new ArrayList<>();
				}
				primeFactors.add(primeFactor);
				n = n.divide(i);
			}
			else
			{
				i = i.add(BigInteger.ONE);
			}
		}

		if (primeFactor.compareTo(n) < 0)
		{
			primeFactors.add(n);
		}
		else
		{
			primeFactors.add(primeFactor);
		}

		return primeFactors;
	}

	public static <T extends Comparable> Set<List<T>> permutations(List<T> items, int permutationLimit){
		Set<List<T>> result = new HashSet<>();
		permutations(items, new Stack<>(), items.size(), permutationLimit, result);
		return result;
	}

	public static <T extends Comparable> void permutations(List<T> items, Stack<T> permutation, int size, int permutationLimit, Set<List<T>> result) {
		Set<List<T>> permutations = new HashSet<>();
		if(result.size() >= permutationLimit){
			return;
		}

		/* permutation stack has become equal to size that we require */
		if(permutation.size() == size) {
			/* print the permutation */
			List<T> newPermutation = new ArrayList<>(permutation);
			if(result.add(newPermutation)){
				System.out.println(newPermutation);
			}
		}

		/* items available for permutation */
		for (int i = 0; i < items.size() && result.size() < permutationLimit; i++)
		{
			T item = items.get(i);
			/* add current item */
			permutation.push(item);

			/* remove item from available item set */
			items.remove(item);

			/* pass it on for next permutation */
			permutations(items, permutation, size, permutationLimit, result);

			/* pop and put the removed item back */
			items.add(permutation.pop());
		}
	}
}
