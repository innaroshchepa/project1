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

@Named(name = "Посада")
@Entity
@Table(name = "posts")
public class Post implements Serializable, Containsable {
	
	private static final long serialVersionUID = -8756788345078147252L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	private long id;
	
	@Named(name = "Назва посади")
	@Column(name = "name", nullable = false)
	private String name;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "post")
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
    	teacher.setPost(this);
    }
    
    public void removeTeacher(Teacher teacher) {
    	teachers.remove(teacher);
        teacher.setPost(null);
    }
    
    @Override
    public String toString() {
    	return name;
    }
    
	@Override
	public boolean containsIgnoreCase(String searchPart) {
		return name.toLowerCase().contains(searchPart);
	}
}