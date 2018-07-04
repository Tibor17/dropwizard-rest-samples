package data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Objects;

import static javax.xml.bind.annotation.XmlAccessType.NONE;

@XmlAccessorType(NONE)
@XmlRootElement
public class XyzReport {
    @XmlAttribute
    @NotNull
    @Min(1)
    private long someData;

    public long getSomeData() {
        return someData;
    }

    public XyzReport setSomeData(long someData) {
        this.someData = someData;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        XyzReport xyzReport = (XyzReport) o;
        return Objects.equals(someData, xyzReport.someData);
    }

    @Override
    public int hashCode() {
        return Objects.hash(someData);
    }
}
