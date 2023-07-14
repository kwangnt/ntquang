package com.teachsync.services.applicationDetail;

import com.teachsync.dtos.applicationDetail.ApplicationDetailReadDTO;
import com.teachsync.entities.ApplicationDetail;
import com.teachsync.repositories.ApplicationDetailRepository;
import com.teachsync.utils.enums.DtoOption;
import com.teachsync.utils.enums.Status;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ApplicationDetailServiceImpl implements ApplicationDetailService {
    @Autowired
    private ApplicationDetailRepository applicationDetailRepository;

    @Autowired
    private ModelMapper mapper;


    /* =================================================== CREATE =================================================== */


    /* =================================================== READ ===================================================== */
    /* applicationId */
    @Override
    public List<ApplicationDetail> getAllByApplicationId(Long applicationId) throws Exception {
        List<ApplicationDetail> detailList =
                applicationDetailRepository.findAllByApplicationIdAndStatusNot(applicationId, Status.DELETED);

        if (detailList.isEmpty()) {
            return null;
        }

        return detailList;
    }
    @Override
    public List<ApplicationDetailReadDTO> getAllDTOByApplicationId(
            Long applicationId, Collection<DtoOption> options) throws Exception {
        List<ApplicationDetail> detailList = getAllByApplicationId(applicationId);

        if (detailList == null) {
            return null;
        }

        return wrapListDTO(detailList, options);
    }

    @Override
    public List<ApplicationDetail> getAllByApplicationIdIn(Collection<Long> applicationIdCollection) throws Exception {
        List<ApplicationDetail> detailList =
                applicationDetailRepository.findAllByApplicationIdInAndStatusNot(applicationIdCollection, Status.DELETED);

        if (detailList.isEmpty()) {
            return null;
        }

        return detailList;
    }
    @Override
    public List<ApplicationDetailReadDTO> getAllDTOByApplicationIdIn(
            Collection<Long> applicationIdCollection, Collection<DtoOption> options) throws Exception {
        List<ApplicationDetail> detailList = getAllByApplicationIdIn(applicationIdCollection);

        if (detailList == null) {
            return null;
        }

        return wrapListDTO(detailList, options);
    }
    @Override
    public Map<Long, List<ApplicationDetailReadDTO>> mapApplicationIdListDTOByApplicationIdIn(
            Collection<Long> applicationIdCollection, Collection<DtoOption> options) throws Exception {
        List<ApplicationDetailReadDTO> detailDTOList = getAllDTOByApplicationIdIn(applicationIdCollection, options);

        if (detailDTOList == null) {
            return new HashMap<>();
        }

        Map<Long, List<ApplicationDetailReadDTO>> applicationIdDTOListMap = new HashMap<>();
        Long applicationId;
        List<ApplicationDetailReadDTO> tmpDetailDTOList;

        for (ApplicationDetailReadDTO detailDTO : detailDTOList) {
            applicationId = detailDTO.getApplicationId();

            tmpDetailDTOList = applicationIdDTOListMap.get(applicationId);

            if (tmpDetailDTOList == null) {
                applicationIdDTOListMap.put(applicationId, new ArrayList<>(List.of(detailDTO)));
            } else {
                tmpDetailDTOList.add(detailDTO);
                applicationIdDTOListMap.put(applicationId, detailDTOList);
            }
        }

        return applicationIdDTOListMap;
    }


    /* =================================================== UPDATE =================================================== */


    /* =================================================== DELETE =================================================== */


    /* =================================================== WRAPPER ================================================== */
    @Override
    public ApplicationDetailReadDTO wrapDTO(
            ApplicationDetail applicationDetail, Collection<DtoOption> options) throws Exception {
        ApplicationDetailReadDTO dto = mapper.map(applicationDetail, ApplicationDetailReadDTO.class);

        /* Add dependency */
        /* TODO:
        if (options != null && !options.isEmpty()) {
            if (options.contains(DtoOption.FK)) {
                FkReadDTO fkDTO = fkService.getDTOById(applicationDetail.getFkId());
                dto.setFk(fkDTO);
            }

            if (options.contains(DtoOption.FK_LIST)) {
                List<FkReadDTO> fkDTOList = fkService.getAllDTOById(applicationDetail.getFkId());
                dto.setFkList(fkDTOList);
            }
        }
        */

        return dto;
    }
    @Override
    public List<ApplicationDetailReadDTO> wrapListDTO(
            Collection<ApplicationDetail> applicationDetailCollection, Collection<DtoOption> options) throws Exception {
        List<ApplicationDetailReadDTO> dtoList = new ArrayList<>();
        ApplicationDetailReadDTO dto;

        /* TODO:
        Map<Long, FkReadDTO> fkIdFkDTOMap = new HashMap<>();
        Map<Long, List<FkReadDTO>> fkIdFkDTOListMap = new HashMap<>();

        if (options != null && !options.isEmpty()) {
            Set<Long> fkIdSet = new HashSet<>();

            for (ApplicationDetail applicationDetail : applicationDetailCollection) {
                fkIdSet.add(applicationDetail.getFkId());
            }

            if (options.contains(DtoOption.FK)) {
                fkIdFkDTOMap = fkService.mapIdDTOByIdIn(fkIdSet);
            }

            if (options.contains(DtoOption.FK_LIST)) {
                fkIdFkDTOListMap = fkService.mapIdListDTOByIdIn(fkIdSet);
            }
        }
        */

        for (ApplicationDetail applicationDetail : applicationDetailCollection) {
            dto = mapper.map(applicationDetail, ApplicationDetailReadDTO.class);

            /* Add dependency */
            /*
            dto.setFk(fkIdFkDTOMap.get(applicationDetail.getFkId()));

            dto.setFkList(fkIdFkDTOListMap.get(applicationDetail.getFkId()));
            */

            dtoList.add(dto);
        }

        return dtoList;
    }
    @Override
    public Page<ApplicationDetailReadDTO> wrapPageDTO(
            Page<ApplicationDetail> applicationDetailPage, Collection<DtoOption> options) throws Exception {
        return new PageImpl<>(
                wrapListDTO(applicationDetailPage.getContent(), options),
                applicationDetailPage.getPageable(),
                applicationDetailPage.getTotalPages());
    }
}
