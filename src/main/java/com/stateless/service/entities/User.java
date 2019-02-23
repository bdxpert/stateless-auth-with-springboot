package com.stateless.service.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.stateless.service.extra.LocalDateTimeDeserializer;
import com.stateless.service.extra.LocalDateTimeSerializer;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.SafeHtml;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * <p>
 * For
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Document
public class User extends Audit implements UserDetails, Serializable {

    private static final long serialVersionUID = 4647970696086355806L;

    @Id
    private String id;

    @Size(min = 0, max = 128, message = "Your First Name must be more than 1 and less than 128 characters.")
    @Pattern(regexp = "^[^@$%]+$", message = "Your First Name can not contain any characters like @, $, %.")
    @NotNull(message = "Your First Name can not be null.")
    @NotEmpty(message = "Your First Name can not be empty.")
    @SafeHtml(message = "Please make sure First Name is properly formatted, containing no malicious characters.")
    private String firstName;

    @Size(min = 0, max = 128, message = "Your Last Name must be more than 1 and less than 128 characters.")
    @Pattern(regexp = "^[^@$%]+$", message = "Your Last Name can not contain any characters like @, $, %.")
    @NotNull(message = "Your Last Name can not be null.")
    @NotEmpty(message = "Your Last Name can not be empty.")
    @SafeHtml(message = "Please make sure Last Name is properly formatted, containing no malicious characters.")
    private String lastName;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Size(min = 8, max = 32, message = "Your password must be more than 8 and less than 32 characters.")
    @NotNull(message = "Password can not be null.")
    @NotEmpty(message = "Password can not be empty.")
    private String password;

    @Indexed(unique = true)
    @Size(min = 3, max = 254, message = "Email must be less than 254 characters.")
    @NotEmpty(message = "You must enter an email address.")
    @SafeHtml(message = "Please make sure your Email Address is properly formatted, containing no malicious characters.")
    @Email (message = "You must enter an valid email address.")
    private String email;

//    @Range(min=1, max=100, message = "Age must be between 1 to 100 years")
//    @NotNull(message = "Age can not be null.")
//    @NotEmpty(message = "Age can not be empty.")
    private int age;

    @JsonIgnore
    @Transient
    private List<GrantedAuthority> authority = AuthorityUtils.createAuthorityList(new String[]{"ROLE_NONE"});


    @JsonIgnore
    private LocalDateTime accessTokenCreationTime;

    @JsonIgnore
    private LocalDateTime accessTokenExpiryTime;

    private String accessToken = "";

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonIgnore
    private LocalDateTime loginDateTime;
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonIgnore
    private LocalDateTime logoutDateTime;
    @JsonIgnore
    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email.toLowerCase();
    }

    @Override
    @JsonIgnore
    public List<GrantedAuthority> getAuthorities() {
        return this.authority;
    }

    @JsonIgnore
    public void setAuthorities(String authority) {
        List<String> roles = new ArrayList<>();
        for (GrantedAuthority grantedAuthority : this.authority) {
            roles.add(grantedAuthority.getAuthority());
        }
        if (authority.length() > 0) {
            roles.add(authority);
        }

        this.authority = AuthorityUtils.createAuthorityList(roles.toArray(new String[roles.size()]));
    }

    @JsonIgnore
    public boolean hasRole(String role) {
        Collection<? extends GrantedAuthority> authorities = this.authority;
        return authorities.contains(new SimpleGrantedAuthority(role));

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email.toLowerCase();
    }

    public List<GrantedAuthority> getAuthority() {
        return authority;
    }

    public void setAuthority(List<GrantedAuthority> authority) {
        this.authority = authority;
    }

    public LocalDateTime getAccessTokenCreationTime() {
        return accessTokenCreationTime;
    }

    public void setAccessTokenCreationTime(LocalDateTime accessTokenCreationTime) {
        this.accessTokenCreationTime = accessTokenCreationTime;
    }

    public LocalDateTime getAccessTokenExpiryTime() {
        return accessTokenExpiryTime;
    }

    public void setAccessTokenExpiryTime(LocalDateTime accessTokenExpiryTime) {
        this.accessTokenExpiryTime = accessTokenExpiryTime;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public LocalDateTime getLoginDateTime() {
        return loginDateTime;
    }

    public void setLoginDateTime(LocalDateTime loginDateTime) {
        this.loginDateTime = loginDateTime;
    }

    public LocalDateTime getLogoutDateTime() {
        return logoutDateTime;
    }

    public void setLogoutDateTime(LocalDateTime logoutDateTime) {
        this.logoutDateTime = logoutDateTime;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @JsonIgnore
    public boolean isResourceAccessTokenActive() {

        if (this.getAccessTokenExpiryTime() == null) {
            return false;
        }
        LocalDateTime accessTokenExpiryTimeLocal = this.getAccessTokenExpiryTime();
        return accessTokenExpiryTimeLocal.isAfter(LocalDateTime.now());
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isEnabled() {
        return true;//!accountEnabled;
    }

}