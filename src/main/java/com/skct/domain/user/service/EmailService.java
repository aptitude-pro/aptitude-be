package com.skct.domain.user.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendVerificationCode(String to, String code) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");
        helper.setTo(to);
        helper.setSubject("[Aptitude Pro] 이메일 인증 코드");
        helper.setText(buildHtml(code), true);
        mailSender.send(message);
    }

    private String buildHtml(String code) {
        return """
            <!DOCTYPE html>
            <html lang="ko">
            <head>
              <meta charset="UTF-8" />
              <meta name="viewport" content="width=device-width, initial-scale=1.0" />
            </head>
            <body style="margin:0;padding:0;background:#f3f4f6;font-family:-apple-system,BlinkMacSystemFont,'Helvetica Neue',Arial,sans-serif;">
              <table width="100%%" cellpadding="0" cellspacing="0" style="background:#f3f4f6;padding:40px 0;">
                <tr>
                  <td align="center">
                    <table width="480" cellpadding="0" cellspacing="0" style="background:#ffffff;border-radius:16px;overflow:hidden;box-shadow:0 4px 24px rgba(0,0,0,0.08);">

                      <!-- 헤더 -->
                      <tr>
                        <td style="background:#111827;padding:28px 40px;">
                          <p style="margin:0;font-size:20px;font-weight:700;color:#ffffff;letter-spacing:-0.3px;">
                            Aptitude <span style="color:#818cf8;">Pro</span>
                          </p>
                          <p style="margin:4px 0 0;font-size:12px;color:#9ca3af;letter-spacing:0.3px;">취업 적성검사 실전 대비 플랫폼</p>
                        </td>
                      </tr>

                      <!-- 본문 -->
                      <tr>
                        <td style="padding:36px 40px 28px;">
                          <p style="margin:0 0 8px;font-size:22px;font-weight:700;color:#111827;">이메일 인증 코드</p>
                          <p style="margin:0 0 28px;font-size:14px;color:#6b7280;line-height:1.6;">
                            아래 6자리 인증 코드를 입력창에 입력하여 이메일 주소를 확인해 주세요.
                          </p>

                          <!-- 인증 코드 박스 -->
                          <table width="100%%" cellpadding="0" cellspacing="0">
                            <tr>
                              <td align="center" style="background:#f3f4f6;border-radius:12px;padding:24px 0;">
                                <p style="margin:0;font-size:38px;font-weight:800;letter-spacing:12px;color:#111827;font-variant-numeric:tabular-nums;">
                                  %s
                                </p>
                              </td>
                            </tr>
                          </table>

                          <p style="margin:20px 0 0;font-size:13px;color:#9ca3af;text-align:center;">
                            이 코드는 발송 후 <strong style="color:#6b7280;">5분</strong> 동안 유효합니다.
                          </p>
                        </td>
                      </tr>

                      <!-- 구분선 -->
                      <tr>
                        <td style="padding:0 40px;">
                          <hr style="border:none;border-top:1px solid #e5e7eb;margin:0;" />
                        </td>
                      </tr>

                      <!-- 안내 문구 -->
                      <tr>
                        <td style="padding:20px 40px 32px;">
                          <p style="margin:0;font-size:12px;color:#9ca3af;line-height:1.7;">
                            본인이 요청하지 않은 경우 이 이메일을 무시하셔도 됩니다.<br/>
                            코드는 타인에게 공유하지 마세요.
                          </p>
                        </td>
                      </tr>

                      <!-- 푸터 -->
                      <tr>
                        <td style="background:#111827;padding:18px 40px;">
                          <p style="margin:0;font-size:12px;color:#4b5563;">
                            © 2025 Aptitude Pro. All rights reserved.
                          </p>
                        </td>
                      </tr>

                    </table>
                  </td>
                </tr>
              </table>
            </body>
            </html>
            """.formatted(code);
    }
}
