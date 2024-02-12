package ro.bogdan_mierloiu.authserver.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "request_admin")
public class RequestAdmin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    private ServerUser user;

    @Column(name = "requested_at")
    private ZonedDateTime requestedAt;

    @Column(name = "status", length = 50)
    @Enumerated(EnumType.STRING)
    private Status status;
}
