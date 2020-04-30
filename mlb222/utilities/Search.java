package mlb222.utilities;

import java.util.List;

// I'm very pleased I have an excuse to use lambda expressions. They're very
// fun!
public interface Search<T> {
  public List<T> find(String substring);
}