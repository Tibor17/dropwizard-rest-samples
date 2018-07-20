import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.As.PROPERTY;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME;
import static com.fasterxml.jackson.databind.SerializationFeature.INDENT_OUTPUT;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * <pre>
 * {
 *   "price" : 123456.1234,
 *   "animals" : [ {
 *     "@type" : "dog",
 *     "name" : "hafan",
 *     "legs" : 4,
 *     "tail" : true,
 *     "dog-birth" : "2001-12-01T18:37:01Z"
 *   }, {
 *     "@type" : "chicken",
 *     "name" : "---",
 *     "legs" : 2,
 *     "wings" : true,
 *     "chicken-birth" : "2001-12-01T18:37:01Z"
 *   } ]
 * }
 * </pre>
 */
public class PolymorphismTest {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");

    private Date date;
    private Calendar calendar;

    @Before
    public void init() throws Exception {
        date = DATE_FORMAT.parse("2001-12-01T18:37:01Z");
        calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        calendar.setTimeInMillis(date.getTime());
    }

    @JsonTypeInfo(use = NAME, include = PROPERTY/*, property = "type"*/)
    @JsonSubTypes({
            @Type(value = Chicken.class, name = "chicken"),
            @Type(value = Dog.class, name = "dog")
    })
    public static class Animal {
        public String name;
        public int legs;
    }

    public static class Chicken extends Animal {
        public boolean wings;

        @JsonProperty("chicken-birth")
        @JsonFormat(shape = STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX", timezone = "UTC")
        public Date birth;
    }

    public static class Dog extends Animal {
        public boolean tail;

        @JsonProperty("dog-birth")
        @JsonFormat(shape = STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX", timezone = "UTC")
        public Calendar birth;
    }

    public static class Pets {
        public BigDecimal price;

        //@JsonProperty("animals")
        /*@JsonSerialize(as = ArrayList.class, contentAs = Animal.class)
        @JsonDeserialize(as = ArrayList.class, contentAs = Animal.class)*/
        public List<Animal> animals = new ArrayList<>();
    }

    @Test
    public void shouldMatchDogs() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        //mapper.enableDefaultTyping();
        mapper.enable(INDENT_OUTPUT);
        mapper.disable(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE);
        mapper.disable(DeserializationFeature.FAIL_ON_NUMBERS_FOR_ENUMS);
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        mapper.disable(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES);

        Dog dog = new Dog();
        dog.name = "hafan";
        dog.legs = 4;
        dog.tail = true;
        dog.birth = calendar;

        Chicken chicken = new Chicken();
        chicken.name = "---";
        chicken.legs = 2;
        chicken.wings = true;
        chicken.birth = date;

        Pets pets = new Pets();
        pets.price = new BigDecimal(new BigInteger("1234561234"), 4);
        pets.animals.add(dog);
        pets.animals.add(chicken);


        String petsJson = mapper.writeValueAsString(pets);
        Pets actualPets = mapper.readValue(petsJson, Pets.class);

        assertThat(actualPets)
                .isNotNull();

        assertThat(actualPets.price)
                .isEqualTo(pets.price);

        assertThat(actualPets.animals.get(0).name)
                .isEqualTo(dog.name);

        assertThat(actualPets.animals.get(0).legs)
                .isEqualTo(dog.legs);

        assertThat(((Dog) actualPets.animals.get(0)).tail)
                .isEqualTo(dog.tail);

        assertThat(((Dog) actualPets.animals.get(0)).birth)
                .isEqualTo(dog.birth);

        assertThat(actualPets.animals.get(1).name)
                .isEqualTo(chicken.name);

        assertThat(actualPets.animals.get(1).legs)
                .isEqualTo(chicken.legs);

        assertThat(((Chicken) actualPets.animals.get(1)).wings)
                .isEqualTo(chicken.wings);

        assertThat(((Chicken) actualPets.animals.get(1)).birth)
                .isEqualTo(chicken.birth);
    }
}
