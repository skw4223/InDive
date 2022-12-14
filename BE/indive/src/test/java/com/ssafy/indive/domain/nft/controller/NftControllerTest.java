package com.ssafy.indive.domain.nft.controller;

import com.ssafy.indive.domain.music.service.dto.ServiceMusicAddRequestDto;
import com.ssafy.indive.domain.nft.controller.dto.WebNftAddRequestDto;
import com.ssafy.indive.domain.nft.controller.dto.WebNftImageGetRequestDto;
import com.ssafy.indive.domain.nft.controller.dto.WebNftModifyRequestDto;
import com.ssafy.indive.domain.nft.exception.NftNotFoundException;
import com.ssafy.indive.domain.nft.exception.NotSatisfiedAmountException;
import com.ssafy.indive.domain.nft.service.NftAddService;
import com.ssafy.indive.domain.nft.service.NftReadService;
import com.ssafy.indive.domain.nft.service.dto.*;
import com.ssafy.indive.domain.nft.service.facade.NftModifyServiceRedisFacade;
import com.ssafy.indive.utils.JacksonUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(NftController.class)
@AutoConfigureMockMvc
@DisplayName("NFT ???????????? ?????? ?????????")
class NftControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NftAddService nftAddService;

    @MockBean
    private NftModifyServiceRedisFacade nftModifyService;

    @MockBean
    private NftReadService nftReadService;

    @Nested
    @DisplayName("[NFT ?????? ??????] NFT??? ????????? ????????? IPFS??? ????????????.")
    class AddImageToIpfs {

        MockMultipartFile image;

        @BeforeEach
        void beforeEach() throws URISyntaxException, IOException {
            image = new MockMultipartFile("file", "image.png", "image/png", Files.newInputStream(Paths.get(ClassLoader.getSystemResource("image.png").toURI())));
        }

        @Test
        @WithMockUser
        @DisplayName("[?????? ?????????]")
        public void success() throws Exception {
            // given
            given(nftAddService.addImageToIpfs(any(ServiceNftAddRequestDto.class))).willReturn(true);

            // when
            ResultActions actions = mockMvc.perform(MockMvcRequestBuilders.multipart("/nft")
                    .file(image)
                    .param("lowerDonationAmount", "1000")
                    .param("stock", "100")
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .with(request -> {
                        request.setMethod("POST");
                        return request;
                    })
                    .with(SecurityMockMvcRequestPostProcessors.csrf()));

            // then
            verify(nftAddService, times(1)).addImageToIpfs(any(ServiceNftAddRequestDto.class));

            actions.andExpect(content().string("true"));
            actions.andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("[?????? ??????] ????????? ????????? lowerDonationAmount, ????????? 0??? ????????????.")
    class CheckStock {

        ServiceNftAmountResponseDto dto = new ServiceNftAmountResponseDto(1000);

        @Test
        @WithMockUser
        @DisplayName("[?????? ????????? 1] ?????? ??????")
        public void success1() throws Exception {
            // given
            given(nftReadService.checkStock(any(ServiceCheckStockGetRequestDto.class))).willReturn(dto);

            // when
            ResultActions actions = mockMvc.perform(MockMvcRequestBuilders.get("/nft/check-stock")
                    .queryParam("artistSeq", "1")
                    .with(SecurityMockMvcRequestPostProcessors.csrf()));

            // then
            verify(nftReadService, times(1)).checkStock(any(ServiceCheckStockGetRequestDto.class));

            actions.andExpect(content().string(JacksonUtil.convertToJson(dto)));
            actions.andExpect(status().isOk());
        }

        @Test
        @WithMockUser
        @DisplayName("[?????? ????????? 2] ?????? ??????????????? nft??? ?????? ?????? 0??? ????????????.")
        public void success2() throws Exception {
            // given
            given(nftReadService.checkStock(any(ServiceCheckStockGetRequestDto.class))).willThrow(NftNotFoundException.class);

            // when
            ResultActions actions = mockMvc.perform(MockMvcRequestBuilders.get("/nft/check-stock")
                    .queryParam("artistSeq", "1")
                    .with(SecurityMockMvcRequestPostProcessors.csrf()));

            // then
            verify(nftReadService, times(1)).checkStock(any(ServiceCheckStockGetRequestDto.class));

            actions.andExpect(content().string("0"));
            actions.andExpect(status().isOk());
        }

        @Test
        @WithMockUser
        @DisplayName("[?????? ?????????] SEQ??? ???????????? ????????? ?????? ??????")
        public void failure() throws Exception {
            // given
            given(nftReadService.checkStock(any(ServiceCheckStockGetRequestDto.class))).willThrow(IllegalArgumentException.class);

            // when
            ResultActions actions = mockMvc.perform(MockMvcRequestBuilders.get("/nft/check-stock")
                    .queryParam("artistSeq", "1")
                    .with(SecurityMockMvcRequestPostProcessors.csrf()));

            // then
            verify(nftReadService, times(1)).checkStock(any(ServiceCheckStockGetRequestDto.class));

            actions.andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("[?????? ??????] ?????? ????????? lowerDonationAmount ???????????? ????????????.")
    class CheckAmount {

        @Test
        @WithMockUser
        @DisplayName("[?????? ?????????] ?????? ??????")
        public void success() throws Exception {
            // given
            given(nftReadService.checkAmount(any(ServiceCheckAmountGetRequestDto.class))).willReturn(true);

            // when
            ResultActions actions = mockMvc.perform(MockMvcRequestBuilders.get("/nft/check-amount")
                    .queryParam("artistSeq", "1")
                    .queryParam("userWallet", "asdf")
                    .queryParam("amount", "1000")
                    .with(SecurityMockMvcRequestPostProcessors.csrf()));

            // then
            verify(nftReadService, times(1)).checkAmount(any(ServiceCheckAmountGetRequestDto.class));

            actions.andExpect(content().string("true"));
            actions.andExpect(status().isOk());
        }

        @Test
        @WithMockUser
        @DisplayName("[?????? ????????? 1] SEQ??? ???????????? ????????? ?????? ??????")
        public void failure1() throws Exception {
            // given
            given(nftReadService.checkAmount(any(ServiceCheckAmountGetRequestDto.class))).willThrow(IllegalArgumentException.class);

            // when
            ResultActions actions = mockMvc.perform(MockMvcRequestBuilders.get("/nft/check-amount")
                    .queryParam("artistSeq", "1")
                    .queryParam("userWallet", "asdf")
                    .queryParam("amount", "1000")
                    .with(SecurityMockMvcRequestPostProcessors.csrf()));

            // then
            verify(nftReadService, times(1)).checkAmount(any(ServiceCheckAmountGetRequestDto.class));

            actions.andExpect(status().isBadRequest());
        }

        @Test
        @WithMockUser
        @DisplayName("[?????? ????????? 2] ?????? ??????????????? nft??? ?????? ??????")
        public void failure2() throws Exception {
            // given
            given(nftReadService.checkAmount(any(ServiceCheckAmountGetRequestDto.class))).willThrow(NftNotFoundException.class);

            // when
            ResultActions actions = mockMvc.perform(MockMvcRequestBuilders.get("/nft/check-amount")
                    .queryParam("artistSeq", "1")
                    .queryParam("userWallet", "asdf")
                    .queryParam("amount", "1000")
                    .with(SecurityMockMvcRequestPostProcessors.csrf()));

            // then
            verify(nftReadService, times(1)).checkAmount(any(ServiceCheckAmountGetRequestDto.class));

            actions.andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("[NFT ??????] NFT??? ?????????????????? ????????? ????????? 1 ???????????????.")
    class IssueNft {

        @Test
        @WithMockUser
        @DisplayName("[?????? ?????????] ?????? ??????")
        public void success() throws Exception {
            // given
            WebNftModifyRequestDto dto = new WebNftModifyRequestDto(1L, "asdf", 1000);

            given(nftModifyService.issueNft(any(ServiceNftModifyRequestDto.class))).willReturn(true);

            // when
            ResultActions actions = mockMvc.perform(MockMvcRequestBuilders.put("/nft")
                    .content(JacksonUtil.convertToJson(dto))
                    .contentType(MediaType.APPLICATION_JSON)
                    .with(SecurityMockMvcRequestPostProcessors.csrf()));

            // then
            actions.andExpect(content().string("true"));
            actions.andExpect(status().isOk());
        }

        @Test
        @WithMockUser
        @DisplayName("[?????? ????????? 1] SEQ??? ???????????? ????????? ?????? ??????")
        public void failure1() throws Exception {
            // given
            WebNftModifyRequestDto dto = new WebNftModifyRequestDto(0L, "asdf", 1000);

            given(nftModifyService.issueNft(any(ServiceNftModifyRequestDto.class))).willThrow(IllegalArgumentException.class);

            // when
            ResultActions actions = mockMvc.perform(MockMvcRequestBuilders.put("/nft")
                    .content(JacksonUtil.convertToJson(dto))
                    .contentType(MediaType.APPLICATION_JSON)
                    .with(SecurityMockMvcRequestPostProcessors.csrf()));

            // then
            verify(nftModifyService, times(1)).issueNft(any(ServiceNftModifyRequestDto.class));

            actions.andExpect(status().isBadRequest());
        }

        @Test
        @WithMockUser
        @DisplayName("[?????? ????????? 2] ?????? ??????????????? nft??? ?????? ??????")
        public void failure2() throws Exception {
            // given
            WebNftModifyRequestDto dto = new WebNftModifyRequestDto(0L, "asdf",1000);

            given(nftModifyService.issueNft(any(ServiceNftModifyRequestDto.class))).willThrow(NftNotFoundException.class);

            // when
            ResultActions actions = mockMvc.perform(MockMvcRequestBuilders.put("/nft")
                    .content(JacksonUtil.convertToJson(dto))
                    .contentType(MediaType.APPLICATION_JSON)
                    .with(SecurityMockMvcRequestPostProcessors.csrf()));

            // then
            verify(nftModifyService, times(1)).issueNft(any(ServiceNftModifyRequestDto.class));

            actions.andExpect(status().isBadRequest());
        }

        @Test
        @WithMockUser
        @DisplayName("[?????? ????????? 3] ?????? ?????? ????????? ????????? ?????? ??????")
        public void failure3() throws Exception {
            // given
            WebNftModifyRequestDto dto = new WebNftModifyRequestDto(1L, "asdf" ,500);

            given(nftModifyService.issueNft(any(ServiceNftModifyRequestDto.class))).willThrow(NotSatisfiedAmountException.class);

            // when
            ResultActions actions = mockMvc.perform(MockMvcRequestBuilders.put("/nft")
                    .content(JacksonUtil.convertToJson(dto))
                    .contentType(MediaType.APPLICATION_JSON)
                    .with(SecurityMockMvcRequestPostProcessors.csrf()));

            // then
            verify(nftModifyService, times(1)).issueNft(any(ServiceNftModifyRequestDto.class));

            actions.andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("[NFT ?????? ??????] IPFS??? ????????? NFT??? ????????????.")
    class GetImage {

        @Test
        @WithMockUser
        @DisplayName("[?????? ?????????] ?????? ??????")
        public void success() throws Exception {
            // given
            given(nftReadService.getImage(any(ServiceNftImageGetRequestDto.class))).willReturn(new UrlResource(ClassLoader.getSystemResource("image.png").toURI()));

            // when
            ResultActions actions = mockMvc.perform(MockMvcRequestBuilders.get("/nft/asdf")
                    .with(SecurityMockMvcRequestPostProcessors.csrf()));

            // then
            actions.andExpect(content().contentType("image/png"));
            actions.andExpect(status().isOk());
        }

        @Test
        @WithMockUser
        @DisplayName("[?????? ?????????] IOexception")
        public void failure() throws Exception {
            // given
            given(nftReadService.getImage(any(ServiceNftImageGetRequestDto.class))).willThrow(IOException.class);

            // when
            ResultActions actions = mockMvc.perform(MockMvcRequestBuilders.get("/nft/asdf")
                    .with(SecurityMockMvcRequestPostProcessors.csrf()));

            // then
            actions.andExpect(status().isInternalServerError());
        }
    }

}