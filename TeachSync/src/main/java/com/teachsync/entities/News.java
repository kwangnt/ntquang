package com.teachsync.entities;

import com.teachsync.utils.enums.Status;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "news")
public class News {
    @Positive
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private Long id;

    @Size(min = 1, max = 45)
    @NotBlank
    @Column(name = "newsTitle", nullable = false, length = 45)
    private String newsTitle;

    @Lob
    @Column(name = "newsDesc")
    private String newsDesc;

    @Lob
    @NotBlank
    @Column(name = "newsLink")
    private String newsLink;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "authorId", referencedColumnName = "id", nullable = false)
    private User author;
    @Positive
    @Column(name = "authorId", insertable = false, updatable = false)
    private Long authorId;

    @Column(name = "status")
    private Status status;


    public News(String newsTitle, String newsDesc, String newsLink, User author, Long authorId, Status status) {
        this.newsTitle = newsTitle;
        this.newsDesc = newsDesc;
        this.newsLink = newsLink;
        this.author = author;
        this.authorId = authorId;
        this.status = status;
    }

    public News(Long id, String newsTitle, String newsDesc, String newsLink, Long authorId, Status status) {
        this.id = id;
        this.newsTitle = newsTitle;
        this.newsDesc = newsDesc;
        this.newsLink = newsLink;
        this.authorId = authorId;
        this.status = status;
    }
}
