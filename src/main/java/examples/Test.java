package examples;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Test {

  public static void main(String[] args) {

    List<Call> collect = Stream.of(
        new Call(1481222000, 1481222020),
        new Call(1481222000, 1481222040),
        new Call(1481222030, 1481222035),
        new Call(1481222020, 1481222060)
    ).collect(Collectors.toList());

    long l = overLapping(collect) - 1;
    System.out.println("final result " + l);
  }

  private static class Call {
    final long startEpoc;
    final long endEpoc;

    private Call(final long startEpoc, final long endEpoc) {
      this.startEpoc = startEpoc;
      this.endEpoc = endEpoc;
    }

    @Override
    public String toString() {
      return "Call{" +
          "startEpoc=" + startEpoc +
          ", endEpoc=" + endEpoc +
          '}';
    }
  }

  private static long overLapping(final List<Call> calls) {
    long maxConcurrent = 0;
    for (Call call : calls) {
      long concurrent = findAllOverlapping(call, calls);
      System.out.println("result " + concurrent + " for " + call);
      if (concurrent > maxConcurrent) {
        maxConcurrent = concurrent;
      }
    }
    return maxConcurrent;
  }

  private static long findAllOverlapping(final Call call, final List<Call> calls) {
    Predicate<Call> check = c -> (call.startEpoc <= c.endEpoc && c.startEpoc <= call.endEpoc);
    return calls.stream().filter(check::test).count();
  }
}
