package ru.fizteh.fivt.students.almazNasibullin.xmlBinder;

import ru.fizteh.fivt.bind.MembersToBind;
import ru.fizteh.fivt.bind.BindingType;

/**
 * 28.11.12
 * @author almaz
 */

@BindingType(MembersToBind.FIELDS)
public class TestClassSerializationFields {
    @AsXmlAttribute(name = "brand")
    private String carBrand = "BMW";
    private String carModel = "X6";
    protected String owner = "Somebody";
    public Long price = 2500000L;
    
    public void setOwner(String owner) {
        this.owner = owner;
    }
    
    public String getOwner() {
        return owner;
    }
    
    public void setCar(String carBrand, String carModel) {
        this.carBrand = carBrand;
        this.carModel = carModel;
    }

    public String getCarBrand() {
        return carBrand;
    }

    public String getCarModel() {
        return carModel;
    }
    
    public void setPrice(Long price) {
        this.price = price;
    }

    public Long getPrice() {
        return price;
    }
    
    @Override
    public boolean equals(Object o) {
        TestClassSerializationFields sf = (TestClassSerializationFields)o;
        return sf.getCarBrand().equals(carBrand) && sf.getCarModel().equals(carModel)
                && sf.getOwner().equals(owner) && sf.getPrice().equals(price);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 71 * hash + (this.carBrand != null ? this.carBrand.hashCode() : 0);
        hash = 71 * hash + (this.carModel != null ? this.carModel.hashCode() : 0);
        hash = 71 * hash + (this.owner != null ? this.owner.hashCode() : 0);
        hash = 71 * hash + (this.price != null ? this.price.hashCode() : 0);
        return hash;
    }
    
    @BindingType(MembersToBind.FIELDS)
    public static class InnerClass {
        private int day = 30;
        int month = 11;

        public int getDay() {
            return day;
        }

        public int getMonth() {
            return month;
        }

        @Override
        public boolean equals(Object o) {
            InnerClass ic = (InnerClass)o;
            return ic.getDay() == day && ic.getMonth() == month;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 41 * hash + this.day;
            hash = 41 * hash + this.month;
            return hash;
        }
    }
}
