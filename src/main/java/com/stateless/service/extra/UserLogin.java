package com.stateless.service.extra;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.SafeHtml;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Created by sanjiv on 2/16/17.
 */
public class UserLogin {
    @NotNull(message = "Password can not be null.")
    @NotEmpty(message = "Password can not be empty.")
    private String password;

    @Size(min = 3, max = 254, message = "Email must be less than 254 characters.")
    @NotEmpty(message = "You must enter an email address.")
    @SafeHtml(message = "Please make sure your Email Address is properly formatted, containing no malicious characters.")
    @Email(message = "You must enter an valid email address.")
    private String email;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "UserLogin{" +
                "password='" + password + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
