#include "TimeAttackRunnerCharacter.h"

#include "GameFramework/CharacterMovementComponent.h"
#include "GameFramework/Controller.h"
#include "GameFramework/SpringArmComponent.h"

#include "Camera/CameraComponent.h"
#include "Components/CapsuleComponent.h"

#include "EnhancedInputComponent.h"
#include "EnhancedInputSubsystems.h"
#include "InputActionValue.h"

#include "Engine/LocalPlayer.h"
#include "Json.h"
#include "JsonUtilities.h"
#include "TimerManager.h"


DEFINE_LOG_CATEGORY(LogTemplateCharacter);

ATimeAttackRunnerCharacter::ATimeAttackRunnerCharacter()
{
	GetCapsuleComponent()->InitCapsuleSize(42.f, 96.0f);
		
	bUseControllerRotationPitch = false;
	bUseControllerRotationYaw = false;
	bUseControllerRotationRoll = false;

	GetCharacterMovement()->bOrientRotationToMovement = true; 	
	GetCharacterMovement()->RotationRate = FRotator(0.0f, 500.0f, 0.0f); 

	GetCharacterMovement()->JumpZVelocity = 700.f;
	GetCharacterMovement()->AirControl = 0.35f;
	GetCharacterMovement()->MaxWalkSpeed = 500.f;
	GetCharacterMovement()->MinAnalogWalkSpeed = 20.f;
	GetCharacterMovement()->BrakingDecelerationWalking = 2000.f;
	GetCharacterMovement()->BrakingDecelerationFalling = 1500.0f;


	CameraBoom = CreateDefaultSubobject<USpringArmComponent>(TEXT("CameraBoom"));
	CameraBoom->SetupAttachment(RootComponent);
	CameraBoom->TargetArmLength = 400.0f; 
	CameraBoom->bUsePawnControlRotation = true; 

	FollowCamera = CreateDefaultSubobject<UCameraComponent>(TEXT("FollowCamera"));
	FollowCamera->SetupAttachment(CameraBoom, USpringArmComponent::SocketName);
	FollowCamera->bUsePawnControlRotation = false; 

	ElapsedTime = 0.0f;
}

void ATimeAttackRunnerCharacter::BeginPlay()
{
	Super::BeginPlay();
}

void ATimeAttackRunnerCharacter::SetupPlayerInputComponent(UInputComponent* PlayerInputComponent)
{
	if (APlayerController* PlayerController = Cast<APlayerController>(GetController()))
	{
		if (UEnhancedInputLocalPlayerSubsystem* Subsystem = ULocalPlayer::GetSubsystem<UEnhancedInputLocalPlayerSubsystem>(PlayerController->GetLocalPlayer()))
		{
			Subsystem->AddMappingContext(DefaultMappingContext, 0);
		}
	}
	
	if (UEnhancedInputComponent* EnhancedInputComponent = Cast<UEnhancedInputComponent>(PlayerInputComponent)) 
	{
		EnhancedInputComponent->BindAction(JumpAction, ETriggerEvent::Started, this, &ACharacter::Jump);
		EnhancedInputComponent->BindAction(JumpAction, ETriggerEvent::Completed, this, &ACharacter::StopJumping);

		EnhancedInputComponent->BindAction(MoveAction, ETriggerEvent::Triggered, this, &ATimeAttackRunnerCharacter::Move);

		EnhancedInputComponent->BindAction(LookAction, ETriggerEvent::Triggered, this, &ATimeAttackRunnerCharacter::Look);
	}
	else
	{
		UE_LOG(LogTemplateCharacter, Error, TEXT("'%s' Failed to find an Enhanced Input component! This template is built to use the Enhanced Input system. If you intend to use the legacy system, then you will need to update this C++ file."), *GetNameSafe(this));
	}
}

void ATimeAttackRunnerCharacter::Move(const FInputActionValue& Value)
{
	FVector2D MovementVector = Value.Get<FVector2D>();

	if (Controller != nullptr)
	{
		const FRotator Rotation = Controller->GetControlRotation();
		const FRotator YawRotation(0, Rotation.Yaw, 0);

		const FVector ForwardDirection = FRotationMatrix(YawRotation).GetUnitAxis(EAxis::X);
	
		const FVector RightDirection = FRotationMatrix(YawRotation).GetUnitAxis(EAxis::Y);

		AddMovementInput(ForwardDirection, MovementVector.Y);
		AddMovementInput(RightDirection, MovementVector.X);
	}
}

void ATimeAttackRunnerCharacter::Look(const FInputActionValue& Value)
{
	FVector2D LookAxisVector = Value.Get<FVector2D>();

	if (Controller != nullptr)
	{
		AddControllerYawInput(LookAxisVector.X);
		AddControllerPitchInput(LookAxisVector.Y);
	}
}

void ATimeAttackRunnerCharacter::StartRecording()
{
	if(ApiUrl.IsEmpty())
	{
		UE_LOG(LogTemp, Error, TEXT("Error: ApiUrl is not set in DefaultGame.ini"));
	}

	Locations.Empty();
	ElapsedTime = 0.0f;

	Locations.Add(GetActorLocation());
	
	UWorld* World = GetWorld();
	if(World)
	{
		RecordingStartTime = World->GetTimeSeconds();

		World->GetTimerManager().SetTimer(RecordingTimerHandle, this, &ATimeAttackRunnerCharacter::OnRecordTimeTick, 0.05f, true);
		UE_LOG(LogTemp, Display, TEXT("Recording Started"));
	}
}

void ATimeAttackRunnerCharacter::OnRecordTimeTick()
{
	UWorld* World = GetWorld();
	if(World)
	{
		ElapsedTime = World->GetTimeSeconds() - RecordingStartTime;
	}

	Locations.Add(GetActorLocation());
}

void ATimeAttackRunnerCharacter::StopRecording()
{
	UWorld* World = GetWorld();	
	if(World)
	{
		World->GetTimerManager().ClearTimer(RecordingTimerHandle);
	}
}

void ATimeAttackRunnerCharacter::SendRecordToServer()
{
	TSharedPtr<FJsonObject> JsonObject = MakeShareable(new FJsonObject);

	JsonObject->SetStringField("username", PlayerName);
	JsonObject->SetNumberField("recordTime", ElapsedTime);

	TArray<TSharedPtr<FJsonValue>> GhostData;
	for (const FVector& Location : Locations)
    {
        TSharedPtr<FJsonObject> Coordinate = MakeShareable(new FJsonObject);
        Coordinate->SetNumberField("x", Location.X);
        Coordinate->SetNumberField("y", Location.Y);
        Coordinate->SetNumberField("z", Location.Z);
        GhostData.Add(MakeShareable(new FJsonValueObject(Coordinate)));
    }
    JsonObject->SetArrayField("ghostData", GhostData);

	FString RequestBody;
    TSharedRef<TJsonWriter<>> JsonWriter = TJsonWriterFactory<>::Create(&RequestBody);
    FJsonSerializer::Serialize(JsonObject.ToSharedRef(), JsonWriter);

	FHttpRequestRef Request = FHttpModule::Get().CreateRequest();
	FString RecordUrl = ApiUrl + TEXT("/api/record");
    Request->SetURL(RecordUrl); 
    Request->SetVerb("POST");
    Request->SetHeader("Content-Type", "application/json");
    Request->SetContentAsString(RequestBody);
    
    Request->OnProcessRequestComplete().BindUObject(this, &ATimeAttackRunnerCharacter::OnResponseReceived);
    Request->ProcessRequest();

	UE_LOG(LogTemp, Display, TEXT("Data Sent. Points: %d"), Locations.Num());
}

void ATimeAttackRunnerCharacter::OnResponseReceived(FHttpRequestPtr Request, FHttpResponsePtr Response, bool bSucceeded)
{
	if(bSucceeded && Response.IsValid())
	{
		if(EHttpResponseCodes::IsOk(Response->GetResponseCode()))
		{
			UE_LOG(LogTemp, Display, TEXT("Server Response: %s"), *Response->GetContentAsString());
		}
		else
		{
			UE_LOG(LogTemp, Error, TEXT("Server Error Code: %d"), Response->GetResponseCode());
		}
	}
	else
	{
		UE_LOG(LogTemp, Error, TEXT("Connection Failed."));
	}
}