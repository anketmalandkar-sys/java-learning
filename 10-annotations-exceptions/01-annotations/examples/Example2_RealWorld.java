import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Example 2: a tiny validation framework driven by annotations on record components.
 *
 * This is how libraries like Jakarta Validation work: you annotate the data, and generic code
 * reads the annotations with reflection and applies the rules.
 *
 * Run: java examples/Example2_RealWorld.java
 */
public class Example2_RealWorld {

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.FIELD, ElementType.RECORD_COMPONENT})
    @interface NotBlank { }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.FIELD, ElementType.RECORD_COMPONENT})
    @interface Range {
        int min() default 0;
        int max() default Integer.MAX_VALUE;
    }

    record SignUp(@NotBlank String name, @NotBlank String email, @Range(min = 18, max = 120) int age) { }

    // Generic: works for ANY object whose fields carry these annotations.
    static List<String> validate(Object target) throws IllegalAccessException {
        List<String> problems = new ArrayList<>();
        for (Field field : target.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            Object value = field.get(target);

            if (field.isAnnotationPresent(NotBlank.class)
                    && (value == null || value.toString().isBlank())) {
                problems.add(field.getName() + " must not be blank");
            }

            Range range = field.getAnnotation(Range.class);
            if (range != null) {
                int n = (Integer) value;
                if (n < range.min() || n > range.max()) {
                    problems.add(field.getName() + " must be between " + range.min() + " and " + range.max());
                }
            }
        }
        return problems;
    }

    public static void main(String[] args) throws Exception {
        List<SignUp> forms = List.of(
                new SignUp("Asha", "asha@mail.com", 31),
                new SignUp(" ", "ravi@mail.com", 16),
                new SignUp("Zoya", "", 130));

        for (SignUp form : forms) {
            List<String> problems = validate(form);
            System.out.println(form.name().isBlank() ? "(no name)" : form.name());
            System.out.println("  " + (problems.isEmpty() ? "valid" : String.join("; ", problems)));
        }
    }
}

/* Expected output:
Asha
  valid
(no name)
  name must not be blank; age must be between 18 and 120
Zoya
  email must not be blank; age must be between 18 and 120
*/
