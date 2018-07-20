import org.junit.Before;
import org.junit.Test;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.annotation.JsonbDateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

public class PolymorphismTest {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    private Date date;
    private Calendar calendar;

    @Before
    public void init() throws Exception {
        date = DATE_FORMAT.parse("2001-01-01");
    }

    public static class Animal {
        public String name;
        public int legs;
    }

    public static class Chicken extends Animal {
        public boolean wings;

        @JsonbDateFormat("yyyy-MM-dd")
        public Date birth;
    }

    public static class Dog extends Animal {
        public boolean tail;

        @JsonbDateFormat("yyyy-MM-dd")
        public Calendar birth;
    }

    @Test
    public void shouldMatchDogs() {
        Dog dog = new Dog();
        dog.name = "hafan";
        dog.legs = 4;
        dog.tail = true;

        //JsonbConfig config = new JsonbConfig().setProperty(JsonbConfigProperties.ZERO_TIME_DEFAULTING, true);
        Jsonb jsonb = JsonbBuilder.create();

        String dogJson = jsonb.toJson(dog);

        Dog actualDog = jsonb.fromJson(dogJson, Dog.class);

        assertThat(actualDog)
                .isNotNull();

        assertThat(actualDog.name)
                .isEqualTo(dog.name);

        assertThat(actualDog.legs)
                .isEqualTo(dog.legs);

        assertThat(actualDog.tail)
                .isEqualTo(dog.tail);
    }

    @Test
    public void shouldMatchChicken() {
        Chicken chicken = new Chicken();
        chicken.name = "---";
        chicken.legs = 2;
        chicken.wings = true;
        //chicken.birth = date;

        Jsonb jsonb = JsonbBuilder.create();

        String chickenJson = jsonb.toJson(chicken);

        Chicken actualChicken = jsonb.fromJson(chickenJson, Chicken.class);

        assertThat(actualChicken)
                .isNotNull();

        assertThat(actualChicken.name)
                .isEqualTo(chicken.name);

        assertThat(actualChicken.legs)
                .isEqualTo(chicken.legs);

        assertThat(actualChicken.wings)
                .isEqualTo(chicken.wings);

        assertThat(actualChicken.birth)
                .isEqualTo(chicken.birth);
    }
}
