package department.database.tables;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import department.database.tables.annotation.Named;

@Named(name = "Відділ")
@Entity
@Table(name = "departments")
public class Department implements Serializable {
	
	private static final long serialVersionUID = -5137947472357908701L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	private long id;
	
	@Named(name = "Назва відділу")
	@Column(name = "name", nullable = false)
	private String name;
	
	@OneToMany(fetch = FetchType.EAGER, mappedBy = "department")
    private Set<Teacher> teachers = new HashSet<>();
	
	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public Set<Teacher> getTeachers() {
		return teachers;
	}
	
    public void addTeacher(Teacher teacher) {
    	teachers.add(teacher);
    	teacher.setDepartment(this);
    }
    
    public void removeTeacher(Teacher teacher) {
    	teachers.remove(teacher);
        teacher.setDepartment(null);
    }
    
    @Override
    public String toString() {
    	return name;
    }
}