package spring.server.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import spring.server.dto.LockerDTO;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Locker {
    @Id @GeneratedValue
    @Column(name = "locker_id")
    private Long id;

    @NotEmpty
    private String lockerName;
    @NotEmpty
    private String address;
    @NotEmpty
    private String addressDetail;
    @NotNull(message = "우편번호 찾기를 수행해야 합니다.")
    private Double latitude;
    @NotNull(message = "우편번호 찾기를 수행해야 합니다.")
    private Double longitude;
    private String qrcode;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user; //호스트

    @JsonIgnore
    @OneToMany(mappedBy = "entrustLocker", cascade = CascadeType.ALL)
    private List<User> customers = new ArrayList<>(); //손님

    public LockerDTO getLockerDTO() {
        return new LockerDTO(this.lockerName, this.address, this.addressDetail
                , this.latitude, this.longitude, this.user.getId());
    }

    public void addCustomer(User user) {
        this.customers.add(user);
    }
}
