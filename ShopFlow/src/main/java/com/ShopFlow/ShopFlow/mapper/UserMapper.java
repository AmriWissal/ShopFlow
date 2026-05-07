package com.ShopFlow.ShopFlow.mapper;

import com.ShopFlow.ShopFlow.dto.request.RegisterRequest;
import com.ShopFlow.ShopFlow.dto.response.UserResponse;
import com.ShopFlow.ShopFlow.entity.User;
import com.ShopFlow.ShopFlow.entity.enums.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    /**
     * Convertit une requête d'inscription en entité User.
     * Ignore les champs générés ou gérés séparément.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", constant = "true")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "sellerProfile", ignore = true)
    @Mapping(target = "addresses", ignore = true)
    User toEntity(RegisterRequest request);

    /**
     * Convertit une entité User en UserResponse DTO.
     * Calcule le nom complet à partir du prénom et du nom.
     */
    @Mapping(target = "fullName", expression = "java(user.getFirstName() + \" \" + user.getLastName())")
    UserResponse toResponse(User user);
}
