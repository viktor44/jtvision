package org.viktor44.jtvision.examples.tvforms;

/**
 * Record type stored by the phone-directory list. 
 */
public class PhoneRecord {

    public static final int TYPE_BUSINESS = 0x1;
    public static final int TYPE_PERSONAL = 0x2;
    public static final int GENDER_MALE = 0;
    public static final int GENDER_FEMALE = 1;

    public String name;
    public String company;
    public String remarks;
    public String phone;
    public int acquaintType; // bitmask of TYPE_*
    public int gender;       // GENDER_*

    public PhoneRecord() {
        name = company = remarks = phone = "";
    }

    public PhoneRecord(String name, String company, String remarks, String phone,
                       int acquaintType, int gender) {
        this.name = name;
        this.company = company;
        this.remarks = remarks;
        this.phone = phone;
        this.acquaintType = acquaintType;
        this.gender = gender;
    }

    /** Tab-separated serialization used by the on-disk record file. */
    public String toLine() {
        return name + '\t' + company + '\t' + remarks + '\t' + phone
            + '\t' + acquaintType + '\t' + gender;
    }

    public static PhoneRecord parse(String line) {
        String[] parts = line.split("\t", -1);
        PhoneRecord r = new PhoneRecord();
        if (parts.length > 0) r.name = parts[0];
        if (parts.length > 1) r.company = parts[1];
        if (parts.length > 2) r.remarks = parts[2];
        if (parts.length > 3) r.phone = parts[3];
        if (parts.length > 4) try { r.acquaintType = Integer.parseInt(parts[4]); } catch (NumberFormatException ignored) {}
        if (parts.length > 5) try { r.gender = Integer.parseInt(parts[5]); } catch (NumberFormatException ignored) {}
        return r;
    }
}
