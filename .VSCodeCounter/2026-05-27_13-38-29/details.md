# Details

Date : 2026-05-27 13:38:29

Directory c:\\Users\\vlad\\IdeaProjects\\PMWorkflowTool\\src

Total : 130 files,  5483 codes, 59 comments, 1771 blanks, all 7313 lines

[Summary](results.md) / Details / [Diff Summary](diff.md) / [Diff Details](diff-details.md)

## Files
| filename | language | code | comment | blank | total |
| :--- | :--- | ---: | ---: | ---: | ---: |
| [src/main/java/com/glassgang/pmworkflow/PmWorkflowApplication.java](/src/main/java/com/glassgang/pmworkflow/PmWorkflowApplication.java) | Java | 9 | 0 | 3 | 12 |
| [src/main/java/com/glassgang/pmworkflow/audit/controller/AuditController.java](/src/main/java/com/glassgang/pmworkflow/audit/controller/AuditController.java) | Java | 35 | 0 | 7 | 42 |
| [src/main/java/com/glassgang/pmworkflow/audit/dto/ProjectAuditLogResponse.java](/src/main/java/com/glassgang/pmworkflow/audit/dto/ProjectAuditLogResponse.java) | Java | 17 | 0 | 4 | 21 |
| [src/main/java/com/glassgang/pmworkflow/audit/entity/ProjectAuditLog.java](/src/main/java/com/glassgang/pmworkflow/audit/entity/ProjectAuditLog.java) | Java | 31 | 0 | 12 | 43 |
| [src/main/java/com/glassgang/pmworkflow/audit/repository/ProjectAuditLogRepository.java](/src/main/java/com/glassgang/pmworkflow/audit/repository/ProjectAuditLogRepository.java) | Java | 25 | 0 | 5 | 30 |
| [src/main/java/com/glassgang/pmworkflow/audit/service/AuditService.java](/src/main/java/com/glassgang/pmworkflow/audit/service/AuditService.java) | Java | 68 | 0 | 14 | 82 |
| [src/main/java/com/glassgang/pmworkflow/auth/controller/AuthController.java](/src/main/java/com/glassgang/pmworkflow/auth/controller/AuthController.java) | Java | 17 | 0 | 4 | 21 |
| [src/main/java/com/glassgang/pmworkflow/auth/dto/LoginRequest.java](/src/main/java/com/glassgang/pmworkflow/auth/dto/LoginRequest.java) | Java | 12 | 0 | 4 | 16 |
| [src/main/java/com/glassgang/pmworkflow/auth/dto/LoginResponse.java](/src/main/java/com/glassgang/pmworkflow/auth/dto/LoginResponse.java) | Java | 11 | 0 | 3 | 14 |
| [src/main/java/com/glassgang/pmworkflow/auth/security/CustomUserDetailsService.java](/src/main/java/com/glassgang/pmworkflow/auth/security/CustomUserDetailsService.java) | Java | 26 | 0 | 6 | 32 |
| [src/main/java/com/glassgang/pmworkflow/auth/service/AuthService.java](/src/main/java/com/glassgang/pmworkflow/auth/service/AuthService.java) | Java | 30 | 0 | 8 | 38 |
| [src/main/java/com/glassgang/pmworkflow/auth/service/JwtService.java](/src/main/java/com/glassgang/pmworkflow/auth/service/JwtService.java) | Java | 29 | 1 | 8 | 38 |
| [src/main/java/com/glassgang/pmworkflow/common/dto/ApiErrorResponse.java](/src/main/java/com/glassgang/pmworkflow/common/dto/ApiErrorResponse.java) | Java | 14 | 0 | 5 | 19 |
| [src/main/java/com/glassgang/pmworkflow/common/dto/PagedResponse.java](/src/main/java/com/glassgang/pmworkflow/common/dto/PagedResponse.java) | Java | 15 | 0 | 4 | 19 |
| [src/main/java/com/glassgang/pmworkflow/common/exception/ApiExceptionHandler.java](/src/main/java/com/glassgang/pmworkflow/common/exception/ApiExceptionHandler.java) | Java | 38 | 0 | 9 | 47 |
| [src/main/java/com/glassgang/pmworkflow/common/exception/BadRequestException.java](/src/main/java/com/glassgang/pmworkflow/common/exception/BadRequestException.java) | Java | 6 | 0 | 2 | 8 |
| [src/main/java/com/glassgang/pmworkflow/common/exception/BusinessRuleException.java](/src/main/java/com/glassgang/pmworkflow/common/exception/BusinessRuleException.java) | Java | 6 | 0 | 2 | 8 |
| [src/main/java/com/glassgang/pmworkflow/common/exception/ForbiddenException.java](/src/main/java/com/glassgang/pmworkflow/common/exception/ForbiddenException.java) | Java | 6 | 0 | 2 | 8 |
| [src/main/java/com/glassgang/pmworkflow/common/exception/NotFoundException.java](/src/main/java/com/glassgang/pmworkflow/common/exception/NotFoundException.java) | Java | 6 | 0 | 2 | 8 |
| [src/main/java/com/glassgang/pmworkflow/common/util/CurrentUserUtil.java](/src/main/java/com/glassgang/pmworkflow/common/util/CurrentUserUtil.java) | Java | 43 | 0 | 14 | 57 |
| [src/main/java/com/glassgang/pmworkflow/config/CorsConfig.java](/src/main/java/com/glassgang/pmworkflow/config/CorsConfig.java) | Java | 39 | 0 | 11 | 50 |
| [src/main/java/com/glassgang/pmworkflow/config/DebugConfig.java](/src/main/java/com/glassgang/pmworkflow/config/DebugConfig.java) | Java | 15 | 0 | 4 | 19 |
| [src/main/java/com/glassgang/pmworkflow/config/FileStorageProperties.java](/src/main/java/com/glassgang/pmworkflow/config/FileStorageProperties.java) | Java | 12 | 0 | 3 | 15 |
| [src/main/java/com/glassgang/pmworkflow/config/JwtAuthenticationFilter.java](/src/main/java/com/glassgang/pmworkflow/config/JwtAuthenticationFilter.java) | Java | 87 | 0 | 23 | 110 |
| [src/main/java/com/glassgang/pmworkflow/config/PasswordConfig.java](/src/main/java/com/glassgang/pmworkflow/config/PasswordConfig.java) | Java | 12 | 0 | 3 | 15 |
| [src/main/java/com/glassgang/pmworkflow/config/SecurityConfig.java](/src/main/java/com/glassgang/pmworkflow/config/SecurityConfig.java) | Java | 52 | 0 | 9 | 61 |
| [src/main/java/com/glassgang/pmworkflow/estimate/controller/BidController.java](/src/main/java/com/glassgang/pmworkflow/estimate/controller/BidController.java) | Java | 102 | 0 | 30 | 132 |
| [src/main/java/com/glassgang/pmworkflow/estimate/dto/BidResponse.java](/src/main/java/com/glassgang/pmworkflow/estimate/dto/BidResponse.java) | Java | 23 | 0 | 5 | 28 |
| [src/main/java/com/glassgang/pmworkflow/estimate/dto/BidRevisionItemCostResponse.java](/src/main/java/com/glassgang/pmworkflow/estimate/dto/BidRevisionItemCostResponse.java) | Java | 36 | 0 | 15 | 51 |
| [src/main/java/com/glassgang/pmworkflow/estimate/dto/BidRevisionItemResponse.java](/src/main/java/com/glassgang/pmworkflow/estimate/dto/BidRevisionItemResponse.java) | Java | 34 | 0 | 27 | 61 |
| [src/main/java/com/glassgang/pmworkflow/estimate/dto/BidRevisionResponse.java](/src/main/java/com/glassgang/pmworkflow/estimate/dto/BidRevisionResponse.java) | Java | 30 | 0 | 5 | 35 |
| [src/main/java/com/glassgang/pmworkflow/estimate/dto/CreateBidRequest.java](/src/main/java/com/glassgang/pmworkflow/estimate/dto/CreateBidRequest.java) | Java | 21 | 0 | 7 | 28 |
| [src/main/java/com/glassgang/pmworkflow/estimate/dto/CreateBidRevisionItemCostRequest.java](/src/main/java/com/glassgang/pmworkflow/estimate/dto/CreateBidRevisionItemCostRequest.java) | Java | 35 | 0 | 14 | 49 |
| [src/main/java/com/glassgang/pmworkflow/estimate/dto/CreateBidRevisionItemRequest.java](/src/main/java/com/glassgang/pmworkflow/estimate/dto/CreateBidRevisionItemRequest.java) | Java | 36 | 0 | 14 | 50 |
| [src/main/java/com/glassgang/pmworkflow/estimate/dto/CreateBidRevisionRequest.java](/src/main/java/com/glassgang/pmworkflow/estimate/dto/CreateBidRevisionRequest.java) | Java | 20 | 0 | 7 | 27 |
| [src/main/java/com/glassgang/pmworkflow/estimate/dto/UpdateBidRevisionItemCostRequest.java](/src/main/java/com/glassgang/pmworkflow/estimate/dto/UpdateBidRevisionItemCostRequest.java) | Java | 28 | 0 | 14 | 42 |
| [src/main/java/com/glassgang/pmworkflow/estimate/dto/UpdateBidRevisionItemRequest.java](/src/main/java/com/glassgang/pmworkflow/estimate/dto/UpdateBidRevisionItemRequest.java) | Java | 28 | 0 | 14 | 42 |
| [src/main/java/com/glassgang/pmworkflow/estimate/entity/Bid.java](/src/main/java/com/glassgang/pmworkflow/estimate/entity/Bid.java) | Java | 53 | 0 | 20 | 73 |
| [src/main/java/com/glassgang/pmworkflow/estimate/entity/BidRevision.java](/src/main/java/com/glassgang/pmworkflow/estimate/entity/BidRevision.java) | Java | 68 | 0 | 28 | 96 |
| [src/main/java/com/glassgang/pmworkflow/estimate/entity/BidRevisionItem.java](/src/main/java/com/glassgang/pmworkflow/estimate/entity/BidRevisionItem.java) | Java | 74 | 0 | 32 | 106 |
| [src/main/java/com/glassgang/pmworkflow/estimate/entity/BidRevisionItemCost.java](/src/main/java/com/glassgang/pmworkflow/estimate/entity/BidRevisionItemCost.java) | Java | 80 | 0 | 34 | 114 |
| [src/main/java/com/glassgang/pmworkflow/estimate/entity/CostElement.java](/src/main/java/com/glassgang/pmworkflow/estimate/entity/CostElement.java) | Java | 40 | 0 | 15 | 55 |
| [src/main/java/com/glassgang/pmworkflow/estimate/entity/CostRate.java](/src/main/java/com/glassgang/pmworkflow/estimate/entity/CostRate.java) | Java | 45 | 0 | 17 | 62 |
| [src/main/java/com/glassgang/pmworkflow/estimate/entity/Customer.java](/src/main/java/com/glassgang/pmworkflow/estimate/entity/Customer.java) | Java | 49 | 0 | 20 | 69 |
| [src/main/java/com/glassgang/pmworkflow/estimate/entity/CustomerAddress.java](/src/main/java/com/glassgang/pmworkflow/estimate/entity/CustomerAddress.java) | Java | 50 | 0 | 20 | 70 |
| [src/main/java/com/glassgang/pmworkflow/estimate/entity/CustomerContact.java](/src/main/java/com/glassgang/pmworkflow/estimate/entity/CustomerContact.java) | Java | 42 | 0 | 17 | 59 |
| [src/main/java/com/glassgang/pmworkflow/estimate/enums/AddressType.java](/src/main/java/com/glassgang/pmworkflow/estimate/enums/AddressType.java) | Java | 7 | 0 | 1 | 8 |
| [src/main/java/com/glassgang/pmworkflow/estimate/enums/BidStatus.java](/src/main/java/com/glassgang/pmworkflow/estimate/enums/BidStatus.java) | Java | 8 | 0 | 1 | 9 |
| [src/main/java/com/glassgang/pmworkflow/estimate/enums/CustomerType.java](/src/main/java/com/glassgang/pmworkflow/estimate/enums/CustomerType.java) | Java | 5 | 0 | 1 | 6 |
| [src/main/java/com/glassgang/pmworkflow/estimate/enums/DepartmentCode.java](/src/main/java/com/glassgang/pmworkflow/estimate/enums/DepartmentCode.java) | Java | 6 | 0 | 1 | 7 |
| [src/main/java/com/glassgang/pmworkflow/estimate/enums/RevisionStatus.java](/src/main/java/com/glassgang/pmworkflow/estimate/enums/RevisionStatus.java) | Java | 8 | 0 | 1 | 9 |
| [src/main/java/com/glassgang/pmworkflow/estimate/mapper/BidMapper.java](/src/main/java/com/glassgang/pmworkflow/estimate/mapper/BidMapper.java) | Java | 137 | 0 | 44 | 181 |
| [src/main/java/com/glassgang/pmworkflow/estimate/repository/BidRepository.java](/src/main/java/com/glassgang/pmworkflow/estimate/repository/BidRepository.java) | Java | 10 | 0 | 6 | 16 |
| [src/main/java/com/glassgang/pmworkflow/estimate/repository/BidRevisionItemCostRepository.java](/src/main/java/com/glassgang/pmworkflow/estimate/repository/BidRevisionItemCostRepository.java) | Java | 17 | 0 | 6 | 23 |
| [src/main/java/com/glassgang/pmworkflow/estimate/repository/BidRevisionItemRepository.java](/src/main/java/com/glassgang/pmworkflow/estimate/repository/BidRevisionItemRepository.java) | Java | 20 | 0 | 7 | 27 |
| [src/main/java/com/glassgang/pmworkflow/estimate/repository/BidRevisionRepository.java](/src/main/java/com/glassgang/pmworkflow/estimate/repository/BidRevisionRepository.java) | Java | 11 | 0 | 6 | 17 |
| [src/main/java/com/glassgang/pmworkflow/estimate/repository/CostElementRepository.java](/src/main/java/com/glassgang/pmworkflow/estimate/repository/CostElementRepository.java) | Java | 10 | 0 | 4 | 14 |
| [src/main/java/com/glassgang/pmworkflow/estimate/repository/CostRateRepository.java](/src/main/java/com/glassgang/pmworkflow/estimate/repository/CostRateRepository.java) | Java | 10 | 0 | 4 | 14 |
| [src/main/java/com/glassgang/pmworkflow/estimate/repository/CustomerAddressRepository.java](/src/main/java/com/glassgang/pmworkflow/estimate/repository/CustomerAddressRepository.java) | Java | 7 | 0 | 3 | 10 |
| [src/main/java/com/glassgang/pmworkflow/estimate/repository/CustomerContactRepository.java](/src/main/java/com/glassgang/pmworkflow/estimate/repository/CustomerContactRepository.java) | Java | 7 | 0 | 3 | 10 |
| [src/main/java/com/glassgang/pmworkflow/estimate/repository/CustomerRepository.java](/src/main/java/com/glassgang/pmworkflow/estimate/repository/CustomerRepository.java) | Java | 8 | 0 | 4 | 12 |
| [src/main/java/com/glassgang/pmworkflow/estimate/service/BidNumberService.java](/src/main/java/com/glassgang/pmworkflow/estimate/service/BidNumberService.java) | Java | 41 | 0 | 11 | 52 |
| [src/main/java/com/glassgang/pmworkflow/estimate/service/BidService.java](/src/main/java/com/glassgang/pmworkflow/estimate/service/BidService.java) | Java | 808 | 16 | 278 | 1,102 |
| [src/main/java/com/glassgang/pmworkflow/file/controller/FileController.java](/src/main/java/com/glassgang/pmworkflow/file/controller/FileController.java) | Java | 55 | 0 | 15 | 70 |
| [src/main/java/com/glassgang/pmworkflow/file/controller/SubstepFileController.java](/src/main/java/com/glassgang/pmworkflow/file/controller/SubstepFileController.java) | Java | 43 | 0 | 9 | 52 |
| [src/main/java/com/glassgang/pmworkflow/file/dto/DeleteSubstepFilesRequest.java](/src/main/java/com/glassgang/pmworkflow/file/dto/DeleteSubstepFilesRequest.java) | Java | 12 | 0 | 4 | 16 |
| [src/main/java/com/glassgang/pmworkflow/file/dto/SubstepFileResponse.java](/src/main/java/com/glassgang/pmworkflow/file/dto/SubstepFileResponse.java) | Java | 16 | 0 | 4 | 20 |
| [src/main/java/com/glassgang/pmworkflow/file/entity/SubstepFile.java](/src/main/java/com/glassgang/pmworkflow/file/entity/SubstepFile.java) | Java | 27 | 0 | 9 | 36 |
| [src/main/java/com/glassgang/pmworkflow/file/repository/SubstepFileRepository.java](/src/main/java/com/glassgang/pmworkflow/file/repository/SubstepFileRepository.java) | Java | 16 | 0 | 5 | 21 |
| [src/main/java/com/glassgang/pmworkflow/file/service/FileStorageService.java](/src/main/java/com/glassgang/pmworkflow/file/service/FileStorageService.java) | Java | 34 | 0 | 14 | 48 |
| [src/main/java/com/glassgang/pmworkflow/file/service/SubstepFileService.java](/src/main/java/com/glassgang/pmworkflow/file/service/SubstepFileService.java) | Java | 171 | 0 | 40 | 211 |
| [src/main/java/com/glassgang/pmworkflow/note/controller/SubstepNoteController.java](/src/main/java/com/glassgang/pmworkflow/note/controller/SubstepNoteController.java) | Java | 26 | 0 | 7 | 33 |
| [src/main/java/com/glassgang/pmworkflow/note/dto/CreateSubstepNoteRequest.java](/src/main/java/com/glassgang/pmworkflow/note/dto/CreateSubstepNoteRequest.java) | Java | 8 | 0 | 2 | 10 |
| [src/main/java/com/glassgang/pmworkflow/note/dto/SubstepNoteResponse.java](/src/main/java/com/glassgang/pmworkflow/note/dto/SubstepNoteResponse.java) | Java | 14 | 0 | 3 | 17 |
| [src/main/java/com/glassgang/pmworkflow/note/entity/SubstepNote.java](/src/main/java/com/glassgang/pmworkflow/note/entity/SubstepNote.java) | Java | 25 | 0 | 8 | 33 |
| [src/main/java/com/glassgang/pmworkflow/note/repository/SubstepNoteRepository.java](/src/main/java/com/glassgang/pmworkflow/note/repository/SubstepNoteRepository.java) | Java | 16 | 0 | 5 | 21 |
| [src/main/java/com/glassgang/pmworkflow/note/service/SubstepNoteService.java](/src/main/java/com/glassgang/pmworkflow/note/service/SubstepNoteService.java) | Java | 83 | 0 | 19 | 102 |
| [src/main/java/com/glassgang/pmworkflow/project/controller/ProjectController.java](/src/main/java/com/glassgang/pmworkflow/project/controller/ProjectController.java) | Java | 54 | 0 | 13 | 67 |
| [src/main/java/com/glassgang/pmworkflow/project/dto/CreateProjectRequest.java](/src/main/java/com/glassgang/pmworkflow/project/dto/CreateProjectRequest.java) | Java | 13 | 0 | 4 | 17 |
| [src/main/java/com/glassgang/pmworkflow/project/dto/ProjectDetailsResponse.java](/src/main/java/com/glassgang/pmworkflow/project/dto/ProjectDetailsResponse.java) | Java | 20 | 0 | 4 | 24 |
| [src/main/java/com/glassgang/pmworkflow/project/dto/ProjectOwnerResponse.java](/src/main/java/com/glassgang/pmworkflow/project/dto/ProjectOwnerResponse.java) | Java | 11 | 0 | 3 | 14 |
| [src/main/java/com/glassgang/pmworkflow/project/dto/ProjectStepResponse.java](/src/main/java/com/glassgang/pmworkflow/project/dto/ProjectStepResponse.java) | Java | 17 | 2 | 7 | 26 |
| [src/main/java/com/glassgang/pmworkflow/project/dto/ProjectStepSummaryResponse.java](/src/main/java/com/glassgang/pmworkflow/project/dto/ProjectStepSummaryResponse.java) | Java | 17 | 0 | 5 | 22 |
| [src/main/java/com/glassgang/pmworkflow/project/dto/ProjectSubstepResponse.java](/src/main/java/com/glassgang/pmworkflow/project/dto/ProjectSubstepResponse.java) | Java | 16 | 0 | 4 | 20 |
| [src/main/java/com/glassgang/pmworkflow/project/dto/ProjectSummaryResponse.java](/src/main/java/com/glassgang/pmworkflow/project/dto/ProjectSummaryResponse.java) | Java | 17 | 0 | 6 | 23 |
| [src/main/java/com/glassgang/pmworkflow/project/dto/RenameProjectRequest.java](/src/main/java/com/glassgang/pmworkflow/project/dto/RenameProjectRequest.java) | Java | 8 | 0 | 3 | 11 |
| [src/main/java/com/glassgang/pmworkflow/project/dto/UpdateStepDeadlineRequest.java](/src/main/java/com/glassgang/pmworkflow/project/dto/UpdateStepDeadlineRequest.java) | Java | 9 | 0 | 4 | 13 |
| [src/main/java/com/glassgang/pmworkflow/project/entity/ComputedStatus.java](/src/main/java/com/glassgang/pmworkflow/project/entity/ComputedStatus.java) | Java | 6 | 0 | 1 | 7 |
| [src/main/java/com/glassgang/pmworkflow/project/entity/Project.java](/src/main/java/com/glassgang/pmworkflow/project/entity/Project.java) | Java | 30 | 1 | 11 | 42 |
| [src/main/java/com/glassgang/pmworkflow/project/entity/ProjectStep.java](/src/main/java/com/glassgang/pmworkflow/project/entity/ProjectStep.java) | Java | 33 | 1 | 13 | 47 |
| [src/main/java/com/glassgang/pmworkflow/project/entity/ProjectSubstep.java](/src/main/java/com/glassgang/pmworkflow/project/entity/ProjectSubstep.java) | Java | 30 | 1 | 12 | 43 |
| [src/main/java/com/glassgang/pmworkflow/project/repository/ProjectRepository.java](/src/main/java/com/glassgang/pmworkflow/project/repository/ProjectRepository.java) | Java | 118 | 1 | 23 | 142 |
| [src/main/java/com/glassgang/pmworkflow/project/repository/ProjectStepRepository.java](/src/main/java/com/glassgang/pmworkflow/project/repository/ProjectStepRepository.java) | Java | 9 | 0 | 4 | 13 |
| [src/main/java/com/glassgang/pmworkflow/project/repository/ProjectSubstepRepository.java](/src/main/java/com/glassgang/pmworkflow/project/repository/ProjectSubstepRepository.java) | Java | 9 | 0 | 4 | 13 |
| [src/main/java/com/glassgang/pmworkflow/project/repository/projection/ProjectStepSummaryRow.java](/src/main/java/com/glassgang/pmworkflow/project/repository/projection/ProjectStepSummaryRow.java) | Java | 14 | 0 | 11 | 25 |
| [src/main/java/com/glassgang/pmworkflow/project/repository/projection/ProjectSummaryFlatRow.java](/src/main/java/com/glassgang/pmworkflow/project/repository/projection/ProjectSummaryFlatRow.java) | Java | 19 | 0 | 16 | 35 |
| [src/main/java/com/glassgang/pmworkflow/project/service/ProjectAccessService.java](/src/main/java/com/glassgang/pmworkflow/project/service/ProjectAccessService.java) | Java | 52 | 0 | 18 | 70 |
| [src/main/java/com/glassgang/pmworkflow/project/service/ProjectMapper.java](/src/main/java/com/glassgang/pmworkflow/project/service/ProjectMapper.java) | Java | 89 | 0 | 26 | 115 |
| [src/main/java/com/glassgang/pmworkflow/project/service/ProjectService.java](/src/main/java/com/glassgang/pmworkflow/project/service/ProjectService.java) | Java | 362 | 8 | 106 | 476 |
| [src/main/java/com/glassgang/pmworkflow/project/service/ProjectStatusService.java](/src/main/java/com/glassgang/pmworkflow/project/service/ProjectStatusService.java) | Java | 91 | 0 | 27 | 118 |
| [src/main/java/com/glassgang/pmworkflow/user/controller/UserController.java](/src/main/java/com/glassgang/pmworkflow/user/controller/UserController.java) | Java | 71 | 0 | 15 | 86 |
| [src/main/java/com/glassgang/pmworkflow/user/dto/AdminResetPasswordRequest.java](/src/main/java/com/glassgang/pmworkflow/user/dto/AdminResetPasswordRequest.java) | Java | 10 | 0 | 4 | 14 |
| [src/main/java/com/glassgang/pmworkflow/user/dto/ChangePasswordRequest.java](/src/main/java/com/glassgang/pmworkflow/user/dto/ChangePasswordRequest.java) | Java | 17 | 0 | 6 | 23 |
| [src/main/java/com/glassgang/pmworkflow/user/dto/CreateUserRequest.java](/src/main/java/com/glassgang/pmworkflow/user/dto/CreateUserRequest.java) | Java | 31 | 0 | 10 | 41 |
| [src/main/java/com/glassgang/pmworkflow/user/dto/UpdateMyProfileRequest.java](/src/main/java/com/glassgang/pmworkflow/user/dto/UpdateMyProfileRequest.java) | Java | 10 | 0 | 4 | 14 |
| [src/main/java/com/glassgang/pmworkflow/user/dto/UpdateUserRequest.java](/src/main/java/com/glassgang/pmworkflow/user/dto/UpdateUserRequest.java) | Java | 24 | 0 | 8 | 32 |
| [src/main/java/com/glassgang/pmworkflow/user/dto/UpdateUserRoleRequest.java](/src/main/java/com/glassgang/pmworkflow/user/dto/UpdateUserRoleRequest.java) | Java | 10 | 0 | 4 | 14 |
| [src/main/java/com/glassgang/pmworkflow/user/dto/UserResponse.java](/src/main/java/com/glassgang/pmworkflow/user/dto/UserResponse.java) | Java | 49 | 0 | 15 | 64 |
| [src/main/java/com/glassgang/pmworkflow/user/entity/AppUser.java](/src/main/java/com/glassgang/pmworkflow/user/entity/AppUser.java) | Java | 24 | 0 | 8 | 32 |
| [src/main/java/com/glassgang/pmworkflow/user/entity/Role.java](/src/main/java/com/glassgang/pmworkflow/user/entity/Role.java) | Java | 25 | 0 | 7 | 32 |
| [src/main/java/com/glassgang/pmworkflow/user/repository/AppUserRepository.java](/src/main/java/com/glassgang/pmworkflow/user/repository/AppUserRepository.java) | Java | 11 | 0 | 6 | 17 |
| [src/main/java/com/glassgang/pmworkflow/user/service/UserService.java](/src/main/java/com/glassgang/pmworkflow/user/service/UserService.java) | Java | 203 | 0 | 71 | 274 |
| [src/main/java/com/glassgang/pmworkflow/workflow/entity/WorkflowTemplate.java](/src/main/java/com/glassgang/pmworkflow/workflow/entity/WorkflowTemplate.java) | Java | 21 | 0 | 7 | 28 |
| [src/main/java/com/glassgang/pmworkflow/workflow/entity/WorkflowTemplateStep.java](/src/main/java/com/glassgang/pmworkflow/workflow/entity/WorkflowTemplateStep.java) | Java | 24 | 0 | 8 | 32 |
| [src/main/java/com/glassgang/pmworkflow/workflow/entity/WorkflowTemplateSubstep.java](/src/main/java/com/glassgang/pmworkflow/workflow/entity/WorkflowTemplateSubstep.java) | Java | 21 | 0 | 7 | 28 |
| [src/main/java/com/glassgang/pmworkflow/workflow/repository/WorkflowTemplateRepository.java](/src/main/java/com/glassgang/pmworkflow/workflow/repository/WorkflowTemplateRepository.java) | Java | 8 | 0 | 4 | 12 |
| [src/main/java/com/glassgang/pmworkflow/workflow/repository/WorkflowTemplateStepRepository.java](/src/main/java/com/glassgang/pmworkflow/workflow/repository/WorkflowTemplateStepRepository.java) | Java | 0 | 0 | 1 | 1 |
| [src/main/java/com/glassgang/pmworkflow/workflow/repository/WorkflowTemplateSubstepRepository.java](/src/main/java/com/glassgang/pmworkflow/workflow/repository/WorkflowTemplateSubstepRepository.java) | Java | 0 | 0 | 1 | 1 |
| [src/main/java/com/glassgang/pmworkflow/workflow/service/WorkflowTemplateService.java](/src/main/java/com/glassgang/pmworkflow/workflow/service/WorkflowTemplateService.java) | Java | 0 | 0 | 1 | 1 |
| [src/main/resources/application-local.yml](/src/main/resources/application-local.yml) | YAML | 34 | 0 | 9 | 43 |
| [src/main/resources/application.yml](/src/main/resources/application.yml) | YAML | 10 | 0 | 3 | 13 |
| [src/main/resources/db/migration/V1\_\_init\_pm\_workflow\_schema.sql](/src/main/resources/db/migration/V1__init_pm_workflow_schema.sql) | MS SQL | 86 | 19 | 14 | 119 |
| [src/main/resources/db/migration/V2\_\_seed\_default\_workflow\_template.sql](/src/main/resources/db/migration/V2__seed_default_workflow_template.sql) | MS SQL | 93 | 9 | 18 | 120 |
| [src/main/resources/db/migration/V3\_\_add\_display\_name\_to\_users.sql](/src/main/resources/db/migration/V3__add_display_name_to_users.sql) | MS SQL | 7 | 0 | 2 | 9 |
| [src/main/resources/db/migration/V4\_\_estimate\_schema\_and\_catalog\_tables.sql](/src/main/resources/db/migration/V4__estimate_schema_and_catalog_tables.sql) | MS SQL | 43 | 0 | 15 | 58 |
| [src/main/resources/db/migration/V5\_\_estimate\_customer.sql](/src/main/resources/db/migration/V5__estimate_customer.sql) | MS SQL | 98 | 0 | 34 | 132 |
| [src/main/resources/db/migration/V6\_\_estimate\_bid\_and\_revision.sql](/src/main/resources/db/migration/V6__estimate_bid_and_revision.sql) | MS SQL | 102 | 0 | 42 | 144 |
| [src/main/resources/db/migration/V7\_\_estimate\_revision\_rows.sql](/src/main/resources/db/migration/V7__estimate_revision_rows.sql) | MS SQL | 142 | 0 | 55 | 197 |
| [src/main/resources/db/migration/V8\_\_estimate\_seed\_catalogs.sql](/src/main/resources/db/migration/V8__estimate_seed_catalogs.sql) | MS SQL | 38 | 0 | 15 | 53 |
| [src/main/resources/db/migration/V9\_\_estimate\_number\_sequences.sql](/src/main/resources/db/migration/V9__estimate_number_sequences.sql) | MS SQL | 6 | 0 | 1 | 7 |

[Summary](results.md) / Details / [Diff Summary](diff.md) / [Diff Details](diff-details.md)