package ro.bogdan_mierloiu.authserver.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_ip")
public class UserIp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ip_address", length = 64, nullable = false)
    private String ipAddress;

    @Column(name = "login_attempts")
    private int loginAttempts;

    @Column(name = "blocked_at")
    private LocalDateTime blockedAt;

    @Column(name = "last_action")
    private LocalDateTime lastAction;

    @Column(name = "was_blocked")
    private Boolean wasBlocked;

    @Column(name = "first_block_end_time")
    private LocalDateTime firstBlockEndTime;

    @Column(name = "second_block_end_time")
    private LocalDateTime secondBlockEndTime;


}
