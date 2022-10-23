package POJO;

import java.util.Arrays;

public class DocumentTypes {

    String id;
    String[] attachmentStages;
    String name;
    String schoolId;
    String description;

    @Override
    public String toString() {
        return "DocumentTypes{" +
                "documentTypeID='" + id + '\'' +
                ", attachmentStages=" + Arrays.toString(attachmentStages) +
                ", name='" + name + '\'' +
                ", schoolID='" + schoolId + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String[] getAttachmentStages() {
        return attachmentStages;
    }

    public void setAttachmentStages(String[] attachmentStages) {
        this.attachmentStages = attachmentStages;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(String schoolID) {
        this.schoolId = schoolID;
    }

}
