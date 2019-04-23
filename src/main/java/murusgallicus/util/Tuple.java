package murusgallicus.util;

public class Tuple<T> {
  private T a1;
  private T a2;

  public Tuple(T first, T second) {
    this.a1 = first;
    this.a2 = second;
  }

  public T getFirst() {
    return a1;
  }

  public T getSecond() {
    return a2;
  }
}
