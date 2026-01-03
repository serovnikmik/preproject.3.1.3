package ru.kata.spring.boot_security.demo.model;

//import javax.persistence.*;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import ru.kata.spring.boot_security.demo.validators.UniqueUsername;

import javax.validation.constraints.*;
import javax.persistence.*;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;


@Entity
public class User implements UserDetails, Serializable {

    @Id
    @Column(name="user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name="name")
    @Size(min = 2, max = 30, message = "Name sholud be 2-30 characters")
    private String name;

    @Column(name="age")
    @Min(value = 0, message = "Age should not be negative")
    @Max(value = 100, message = "Are you really more than 100 years old?")
    private int age;

    @Column(name="email")
    @NotBlank(message = "Email should not be empty")
    @Email(message = "Email should be valid")
    private String email;

    // -=-=-=-=-=-=-=-=-=-=-=-=-=
    @Column(name = "username", unique = true, nullable = false)
    @NotEmpty(message = "Username should not be empty")
    @UniqueUsername
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();
    // -=-=-=-=-=-=-=-=-=-=-=-=-=

    public User(){

    }

    public User(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public User(String name, int age, String email) {
        this.name = name;
        this.age = age;
        this.email = email;
    }

    public User(int id, String name, int age) {
        this.name = name;
        this.age = age;
        this.id = id;
    }

    public User(int id, String name, int age, String email) {
        this.name = name;
        this.age = age;
        this.id = id;
        this.email = email;
    }

    public User(String username, String password, String name, int age, String email) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.age = age;
        this.email = email;
    }

    // -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return getRoles(); // Role implements GrantedAuthority
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // Аккаунт не просрочен
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // Аккаунт не заблокирован
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Пароль не просрочен
    }

    @Override
    public boolean isEnabled() {
        return true; // Аккаунт включен
    }
    // -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getEmail(){
        return this.email;
    }

    public void setEmail(String email){
        this.email = email;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", age=" + age +
                ", email='" + email + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", roles=" + roles +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id == user.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}
