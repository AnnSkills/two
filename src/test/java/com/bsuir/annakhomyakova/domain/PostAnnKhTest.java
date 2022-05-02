package com.bsuir.annakhomyakova.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.bsuir.annakhomyakova.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PostAnnKhTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(PostAnnKh.class);
        PostAnnKh postAnnKh1 = new PostAnnKh();
        postAnnKh1.setId(1L);
        PostAnnKh postAnnKh2 = new PostAnnKh();
        postAnnKh2.setId(postAnnKh1.getId());
        assertThat(postAnnKh1).isEqualTo(postAnnKh2);
        postAnnKh2.setId(2L);
        assertThat(postAnnKh1).isNotEqualTo(postAnnKh2);
        postAnnKh1.setId(null);
        assertThat(postAnnKh1).isNotEqualTo(postAnnKh2);
    }
}
