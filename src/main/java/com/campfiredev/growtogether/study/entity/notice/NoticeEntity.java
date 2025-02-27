package com.campfiredev.growtogether.study.entity.notice;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "study_notice")
//BaseEntity 상속 예정
public class NoticeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "notice_id")
  private Long id;

  @Column(nullable = false)
  private String title;

  @Column(columnDefinition = "TEXT", nullable = false)
  private String content;

//  @ManyToOne(fetch = FetchType.LAZY)
//  @JoinColumn(name = "study_id", nullable = false)
//  @OnDelete(action = OnDeleteAction.CASCADE)
//  private StudyEntity study;

  //임시
  private Long studyId;

  public NoticeEntity updateNotice(String title, String content){
    this.title = title;
    this.content = content;

    return this;
  }
}
