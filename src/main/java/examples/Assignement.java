package examples;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Assignement {


  public static void main(String[] args) {
    int safePosition = findSafePosition(1, 10);
    System.out.println("safePosition " + safePosition);
  }


  private static int findSafePosition(final int sylLen, final int numberOfPrisoners) {

    List<Integer> prisoners = IntStream.rangeClosed(1, numberOfPrisoners).boxed().collect(Collectors.toList());

    int myLoc = 0;
    // For java list indexing starts with 0 so size will be always 1 less then the given values
    final int numberOfSyllables = sylLen - 1;

    while (prisoners.size() != 1) {
      // taking modulus so that once above the size then it should go back to previous element
      final int shootLocation = (myLoc + numberOfSyllables) % prisoners.size();
      prisoners.remove(shootLocation);
      // Once we remove from n-1 location next right element(nth) will be moved n-1
      myLoc = shootLocation; // +1 is not done as list shifts right element to removed position
    }

    return prisoners.get(0);
  }
}
