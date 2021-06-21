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
import department.database.tables.teacher.Teacher;

@Named(name = "Відділ")
@Entity
@Table(name = "departments")
public class Department implements Serializable, Comparable<Department>, Containsable {
	
	private static final long serialVersionUID = -5137947472357908701L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	private long id;
	
	@Named(name = "Назва відділу")
	@Column(name = "name", unique = true, nullable = false)
	private String name;
	
	@Named(name = "Показувати години в таблиці")
	@Column(name = "is_bet_in_table")
	private Boolean isBetInTable = false;
	
	@Named(name = "Заступник директора (ПІБ)")
	@Column(name = "deputy_director")
	private String deputyDirector;
	
	@Named(name = "Посада заступника директора")
	@Column(name = "deputy_director_with")
	private String deputyDirectorWith;	
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "department")
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
	
	public boolean getIsBetInTable() {
		return isBetInTable == null ? false : isBetInTable.booleanValue();
	}
	
	public void setBetInTable(Boolean isBetInTable) {
		this.isBetInTable = isBetInTable;
	}
	
	public String getDeputyDirector() {
		return deputyDirector;
	}
	
	public void setDeputyDirector(String deputyDirector) {
		this.deputyDirector = deputyDirector;
	}
	
	public String getDeputyDirectorWith() {
		return deputyDirectorWith;
	}
	
	public void setDeputyDirectorWith(String deputyDirectorWith) {
		this.deputyDirectorWith = deputyDirectorWith;
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
    
    @Override
	public int compareTo(Department department) {
		return name.compareTo(department.getName());
	}

	@Override
	public boolean containsIgnoreCase(String searchPart) {
		return name.toLowerCase().contains(searchPart);
	}
}