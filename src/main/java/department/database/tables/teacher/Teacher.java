package department.database.tables.teacher;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import department.database.tables.Department;
import department.database.tables.Post;
import department.database.tables.Containsable;
import department.database.tables.annotation.Named;

@Named(name = "Співробітник")
@Entity
@Table(name = "teachers")
public class Teacher implements Serializable, Containsable {
	
	private static final long serialVersionUID = 1988549743993862853L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	private long id;
	
	@Named(name = "ПІБ Співробітника")
	@Column(name = "name", nullable = false)
	private String name;
	
	@Named(name = "Стать")
	@Column(name = "gender")
	@Enumerated(EnumType.ORDINAL)
	private GenderType gender = GenderType.MALE;
	
	@Named(name = "Посада")
	@ManyToOne(fetch = FetchType.LAZY)
	@Cascade(value = CascadeType.ALL)
    @JoinColumn(name = "post", nullable = true)
	private Post post;
	
	@Named(name = "Відділ")
	@ManyToOne(fetch = FetchType.LAZY)
	@Cascade(value = CascadeType.ALL)
    @JoinColumn(name = "department", nullable = true)
	private Department department;
	
	@Named(name = "Ставка")
	@Column(name = "bet")
	private float bet = 1;
	
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
	
	public GenderType getGender() {
		return gender;
	}
	
	public void setGender(GenderType gender) {
		this.gender = gender;
	}
	
	public Post getPost() {
		return post;
	}
	
	public void setPost(Post post) {
		this.post = post;
	}
	
	public Department getDepartment() {
		return department;
	}
	
	public void setDepartment(Department department) {
		this.department = department;
	}
	
	public float getBet() {
		return bet;
	}
	
	public void setBet(float bet) {
		this.bet = bet;
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