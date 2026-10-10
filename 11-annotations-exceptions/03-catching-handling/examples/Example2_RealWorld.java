import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Example 2: try-with-resources in depth, with simulated resources (no real files needed).
 *
 *   - resources close automatically, in reverse order
 *   - catch and finally run AFTER the resources are closed
 *   - a failing close() is attached to the main exception as "suppressed"
 *   - an effectively final variable can be used as a resource (Java 9+)
 *
 * Run: java examples/Example2_RealWorld.java
 */
public class Example2_RealWorld {

    static final List<String> EVENTS = new ArrayList<>();

    // A pretend connection. close() can be told to fail.
    static class Connection implements AutoCloseable {
        private final String name;
        private final boolean failOnClose;

        Connection(String name, boolean failOnClose) {
            this.name = name;
            this.failOnClose = failOnClose;
            EVENTS.add("open " + name);
        }

        void send(String data) throws IOException {
            if (data.contains("!")) {
                throw new IOException(name + " rejected '" + data + "'");
            }
            EVENTS.add(name + " sent " + data);
        }

        @Override
        public void close() throws IOException {         // narrower than AutoCloseable's "throws Exception"
            EVENTS.add("close " + name);
            if (failOnClose) {
                throw new IOException(name + " failed to close");
            }
        }
    }

    static void run(String title, String data, boolean failOnClose) {
        EVENTS.clear();
        try (Connection db = new Connection("db", false);
             Connection queue = new Connection("queue", failOnClose)) {
            db.send(data);
            queue.send(data);
        } catch (IOException e) {
            EVENTS.add("catch: " + e.getMessage());
            for (Throwable s : e.getSuppressed()) {
                EVENTS.add("  suppressed: " + s.getMessage());
            }
        } finally {
            EVENTS.add("finally");
        }
        System.out.println(title);
        EVENTS.forEach(e -> System.out.println("  " + e));
    }

    public static void main(String[] args) throws IOException {
        run("1. normal run: closed in reverse order, then finally", "order-1", false);
        run("2. body fails: resources closed BEFORE catch runs", "bad!", false);
        run("3. body and close both fail: the close failure is suppressed", "bad!", true);
        run("4. only close fails: it becomes the main exception", "order-2", true);

        // Java 9+: an effectively final variable declared earlier can be the resource.
        EVENTS.clear();
        Connection cache = new Connection("cache", false);
        try (cache) {
            cache.send("warm-up");
        }
        System.out.println("5. existing variable as a resource");
        EVENTS.forEach(e -> System.out.println("  " + e));
    }
}

/* Expected output:
1. normal run: closed in reverse order, then finally
  open db
  open queue
  db sent order-1
  queue sent order-1
  close queue
  close db
  finally
2. body fails: resources closed BEFORE catch runs
  open db
  open queue
  close queue
  close db
  catch: db rejected 'bad!'
  finally
3. body and close both fail: the close failure is suppressed
  open db
  open queue
  close queue
  close db
  catch: db rejected 'bad!'
    suppressed: queue failed to close
  finally
4. only close fails: it becomes the main exception
  open db
  open queue
  db sent order-2
  queue sent order-2
  close queue
  close db
  catch: queue failed to close
  finally
5. existing variable as a resource
  open cache
  cache sent warm-up
  close cache
*/
