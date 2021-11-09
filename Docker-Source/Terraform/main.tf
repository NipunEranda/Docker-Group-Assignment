terraform {
  required_providers {
    azurerm = {
      source  = "hashicorp/azurerm"
      version = "=2.48.0"
    }
  }
}

locals {
 env_variables = {
   DOCKER_REGISTRY_SERVER_URL            = "https://ccContainerRegistry1.azurecr.io"
   DOCKER_REGISTRY_SERVER_USERNAME       = "0c2f4e50-746a-4535-b368-cae13522a34b"
   DOCKER_REGISTRY_SERVER_PASSWORD       = "q0yg-Hknd4yPUtTn.6y.3e_Gp7gKqeJ0un"
 }
}

provider "azurerm" {
  features {}
}

provider "azuread" {
 version = "=2.8.0"
}

resource "azurerm_resource_group" "rg" {
  name     = "CC-Group-Resources"
  location = "eastus"
}

resource "azurerm_container_registry" "acr" {
  name                = "ccContainerRegistry1"
  resource_group_name = azurerm_resource_group.rg.name
  location            = azurerm_resource_group.rg.location
  sku                 = "Basic"
  admin_enabled       = true
}

resource "azurerm_kubernetes_cluster" "cluster" {
  name                = "cc-cluster"
  location            = azurerm_resource_group.rg.location
  resource_group_name = azurerm_resource_group.rg.name
  dns_prefix          = "cc"

  enable_attach_acr = true
  acr_id            = azurerm_container_registry.acr.id

  default_node_pool {
    name       = "default"
    node_count = "2"
    vm_size    = "standard_d2_v2"
  }

  identity {
    type = "SystemAssigned"
  }
}